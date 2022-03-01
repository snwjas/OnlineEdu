package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.mapper.*;
import xyz.refrain.onlineedu.model.entity.*;
import xyz.refrain.onlineedu.model.enums.CourseStatusEnum;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduCourseDetailVO;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程 服务(为讲师二次修改课程服务)
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduCourseTmpService {

	@Resource
	private EduCourseMapper eduCourseMapper;

	@Resource
	private EduChapterMapper eduChapterMapper;

	@Resource
	private EduChapterTmpMapper eduChapterTmpMapper;

	@Resource
	private EduVideoMapper eduVideoMapper;

	@Resource
	private EduVideoTmpMapper eduVideoTmpMapper;

	@Autowired
	private EduCourseService eduCourseService;

	@Autowired
	private AliyunVodService aliyunVodService;

	/**
	 * 讲师第二次以上修改课程信息并提交
	 */
	public R secondUpdate(EduCourseDetailVO vo, MultipartFile file) throws IOException {
		// 检查课程是否是审核状态
		if (eduCourseService.checkCourseIsAuditing(vo.getId())) {
			return RUtils.fail("非法更改，课程正在审核中");
		}
		R r = eduCourseService.update(vo, file);
		if (r.getStatus() == RS.SUCCESS.status()) {
			boolean b = eduCourseService.changeStatus(vo.getId(), CourseStatusEnum.SECOND_AUDITING);
			if (b) {
				return RUtils.success("课程修改已成功提交审核");
			}
		}
		return RUtils.fail("系统错误，提交失败");
	}

	/**
	 * 讲师第二次以上修改课程章节和视频
	 * 复制原表的数据到临时表中
	 */
	public void moveChapterAndVideoToTempFromOriginal(int courseId) {
		// 判断临时表是否已经存在该课程了，防止讲师多次点击修改
		Integer count = eduChapterTmpMapper.selectCount(
				Wrappers.lambdaQuery(EduChapterTmpEntity.class)
						.eq(EduChapterTmpEntity::getCourseId, courseId)
		);
		// 已经存在数据了
		if (count > 0) {
			return;
		}
		// 1. 复制章节信息到临时表
		List<EduChapterEntity> chapterEntityList = eduChapterMapper.selectList(
				Wrappers.lambdaQuery(EduChapterEntity.class)
						.eq(EduChapterEntity::getCourseId, courseId)
		);
		for (EduChapterEntity chapterEntity : chapterEntityList) {
			EduChapterTmpEntity chapterTmpEntity = new EduChapterTmpEntity();
			// 属性设置
			BeanUtils.copyProperties(chapterEntity, chapterTmpEntity, "id");
			chapterTmpEntity.setOid(chapterEntity.getId());
			// 执行插入
			eduChapterTmpMapper.insert(chapterTmpEntity);
			// 返回主键ID
			long chapterId = chapterTmpEntity.getId();

			// 2. 复制章节下的视频信息
			List<EduVideoEntity> videoEntityList = eduVideoMapper.selectList(
					Wrappers.lambdaQuery(EduVideoEntity.class)
							.eq(EduVideoEntity::getCourseId, courseId)
							.eq(EduVideoEntity::getChapterId, chapterEntity.getId())
			);
			for (EduVideoEntity videoEntity : videoEntityList) {
				EduVideoTmpEntity videoTmpEntity = new EduVideoTmpEntity();
				// 属性设置
				BeanUtils.copyProperties(videoEntity, videoTmpEntity, "id");
				videoTmpEntity.setOid(videoEntity.getId());
				videoTmpEntity.setChapterId(chapterId);
				// 执行插入
				eduVideoTmpMapper.insert(videoTmpEntity);
			}
		}
	}

	/**
	 * 通过二次审核
	 * 讲师第二次以上修改课程章节和视频
	 * 复制临时表中的数据到原表（通过二次审核）
	 */
	public void moveChapterAndVideoToOriginalFromTemp(int courseId) {
		// 判断临时表是否已经存在该课程了
		Integer count = eduChapterTmpMapper.selectCount(
				Wrappers.lambdaQuery(EduChapterTmpEntity.class)
						.eq(EduChapterTmpEntity::getCourseId, courseId)
		);
		// 临时表中没有数据,直接返回
		if (count < 1) {
			return;
		}
		// 1. 对比两表，找出被删除的旧视频数据，即old[1,2,3],new[1,3,4]，目标为diff[2]
		// 此原表视频集合<EduVideoEntity.id, EduVideoEntity.videoId>
		HashMap<Integer, String> oldIdMap = new HashMap<>();
		eduVideoMapper.selectList(
				Wrappers.lambdaQuery(EduVideoEntity.class)
						.select(EduVideoEntity::getId, EduVideoEntity::getVideoId)
						.eq(EduVideoEntity::getCourseId, courseId)
		).stream().parallel().forEach(e -> oldIdMap.put(e.getId(), e.getVideoId()));
		// 删除数据（视频资源、视频记录、章节记录）
		// 删除视频资源
		Set<Integer> remainingOldIdSet = eduVideoTmpMapper.selectList(
				Wrappers.lambdaQuery(EduVideoTmpEntity.class)
						.select(EduVideoTmpEntity::getOid)
						.eq(EduVideoTmpEntity::getCourseId, courseId)
						.gt(EduVideoTmpEntity::getOid, 0)
		).stream().parallel().map(EduVideoTmpEntity::getOid).collect(Collectors.toSet());
		// 遍历查找需要删除视频资源ID
		List<String> deletedVideoSourceIds = new ArrayList<>();
		oldIdMap.forEach((vid, vsid) -> {
			if (!remainingOldIdSet.contains(vid)) {
				deletedVideoSourceIds.add(vsid);
			}
		});
		aliyunVodService.deleteVideos(deletedVideoSourceIds);
		// 删除原表章节记录
		eduChapterMapper.delete(
				Wrappers.lambdaQuery(EduChapterEntity.class)
						.eq(EduChapterEntity::getCourseId, courseId)
		);
		// 删除原表视频记录
		eduVideoMapper.delete(
				Wrappers.lambdaQuery(EduVideoEntity.class)
						.eq(EduVideoEntity::getCourseId, courseId)
		);

		// 2. 复制临时表章节信息到原表
		List<EduChapterTmpEntity> chapterTmpEntityList = eduChapterTmpMapper.selectList(
				Wrappers.lambdaQuery(EduChapterTmpEntity.class)
						.eq(EduChapterTmpEntity::getCourseId, courseId)
		);
		for (EduChapterTmpEntity chapterTmpEntity : chapterTmpEntityList) {
			EduChapterEntity chapterEntity = new EduChapterEntity();
			// 属性设置
			BeanUtils.copyProperties(chapterTmpEntity, chapterEntity, "id");
			// 执行插入
			eduChapterMapper.insert(chapterEntity);
			// 返回主键ID
			int chapterId = chapterEntity.getId();

			// 3. 复制临时表章节下的视频信息到原表
			List<EduVideoTmpEntity> videoTmpEntityList = eduVideoTmpMapper.selectList(
					Wrappers.lambdaQuery(EduVideoTmpEntity.class)
							.eq(EduVideoTmpEntity::getCourseId, courseId)
							.eq(EduVideoTmpEntity::getChapterId, chapterTmpEntity.getId())
			);
			for (EduVideoTmpEntity videoTmpEntity : videoTmpEntityList) {
				EduVideoEntity videoEntity = new EduVideoEntity();
				// 属性设置
				BeanUtils.copyProperties(videoTmpEntity, videoEntity, "id");
				videoEntity.setChapterId(chapterId);
				// 执行插入
				eduVideoMapper.insert(videoEntity);
			}
		}
	}

	/**
	 * 删除临时表课程数据（章节和视频）
	 *
	 * @param courseId             课程ID
	 * @param deleteNewVideoSource 是否删除新添加的视频资源
	 */
	public void deleteChapterAndVideoTemp(int courseId, boolean deleteNewVideoSource) {
		// 删除章节记录
		eduChapterTmpMapper.delete(
				Wrappers.lambdaQuery(EduChapterTmpEntity.class)
						.eq(EduChapterTmpEntity::getCourseId, courseId)
		);
		// 删除新添加的视频资源ID
		if (deleteNewVideoSource) {
			List<String> videoIds = eduVideoTmpMapper.selectList(
					Wrappers.lambdaQuery(EduVideoTmpEntity.class)
							.select(EduVideoTmpEntity::getVideoId)
							.eq(EduVideoTmpEntity::getCourseId, courseId)
							.gt(EduVideoTmpEntity::getOid, 0)
			).stream().parallel()
					.map(EduVideoTmpEntity::getVideoId)
					.filter(StringUtils::hasText)
					.collect(Collectors.toList());
			aliyunVodService.deleteVideos(videoIds);
		}
		// 删除视频记录
		eduVideoTmpMapper.delete(
				Wrappers.lambdaQuery(EduVideoTmpEntity.class)
						.eq(EduVideoTmpEntity::getCourseId, courseId)
		);
	}


}
