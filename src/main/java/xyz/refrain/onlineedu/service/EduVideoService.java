package xyz.refrain.onlineedu.service;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.mapper.EduCourseMapper;
import xyz.refrain.onlineedu.mapper.EduVideoMapper;
import xyz.refrain.onlineedu.mapper.RelCourseMemberMapper;
import xyz.refrain.onlineedu.model.entity.EduCourseEntity;
import xyz.refrain.onlineedu.model.entity.EduVideoEntity;
import xyz.refrain.onlineedu.model.entity.RelCourseMemberEntity;
import xyz.refrain.onlineedu.model.securtiy.UctrMemberDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoVO;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;
import xyz.refrain.onlineedu.utils.VideoUtil;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 视频 service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduVideoService {

	@Resource
	private EduVideoMapper eduVideoMapper;

	@Resource
	private EduCourseMapper eduCourseMapper;

	@Autowired
	private AliyunVodService aliyunVodService;

	@Resource
	private RelCourseMemberMapper relCourseMemberMapper;


	/**
	 * 获取视频数据
	 */
	public List<EduVideoVO> listVideos(int chapterId) {
		List<EduVideoEntity> entityList = eduVideoMapper.selectList(
				Wrappers.lambdaQuery(EduVideoEntity.class)
						.eq(EduVideoEntity::getChapterId, chapterId)
						.orderByAsc(EduVideoEntity::getSort)
						.orderByAsc(EduVideoEntity::getTitle)
		);
		if (CollectionUtils.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		return covertToVO(entityList);
	}

	/**
	 * 新建视频(上传视频)
	 */
	public R create(int courseId, int chapterId, MultipartFile file) {
		// 视频文件限制在1GB
		if (file.getSize() > 1073741824L) {
			return RUtils.fail("视频文件不能超过1GB");
		}
		// 上传视频
		String videoId = aliyunVodService.uploadVideo(file);
		if (!StringUtils.hasText(videoId)) {
			String msg = StrUtil.format("视频文件[{}]上传失败", file.getOriginalFilename());
			return RUtils.fail(msg);
		}
		// 执行插入
		EduVideoEntity entity = new EduVideoEntity()
				.setCourseId(courseId)
				.setChapterId(chapterId)
				.setTitle(FileNameUtil.mainName(file.getOriginalFilename()))
				.setVideoId(videoId)
				.setPlayCount(0)
				.setDuration(VideoUtil.ReadVideoTimeMs(file))
				.setSort(0)
				.setSize(file.getSize())
				.setFree(false);
		int i = eduVideoMapper.insert(entity);
		// 总课时+1
		if (i > 0) {
			Integer count = eduVideoMapper.selectCount(
					Wrappers.lambdaQuery(EduVideoEntity.class)
							.eq(EduVideoEntity::getCourseId, courseId)
			);
			eduCourseMapper.update(null,
					Wrappers.lambdaUpdate(EduCourseEntity.class)
							.eq(EduCourseEntity::getId, courseId)
							.set(EduCourseEntity::getLessonNum, count)
			);
		}
		// 返回
		String msg = StrUtil.format("视频文件[{}]上传成功", file.getOriginalFilename());
		return RUtils.success(msg, entity);
	}

	/**
	 * 更新视频信息
	 */
	public R update(EduVideoVO vo) {
		String title = vo.getTitle();
		Integer sort = vo.getSort();
		Boolean free = vo.getFree();
		int i = eduVideoMapper.update(null,
				Wrappers.lambdaUpdate(EduVideoEntity.class)
						.eq(EduVideoEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), EduVideoEntity::getTitle, title)
						.set(Objects.nonNull(sort), EduVideoEntity::getSort, sort)
						.set(Objects.nonNull(free), EduVideoEntity::getFree, free)
		);
		return RUtils.commonFailOrNot(i, "视频信息更新");
	}

	/**
	 * 删除视频
	 */
	public R delete(int id) {
		EduVideoEntity entity = eduVideoMapper.selectOne(
				Wrappers.lambdaQuery(EduVideoEntity.class)
						.select(EduVideoEntity::getVideoId)
						.eq(EduVideoEntity::getId, id)
		);
		if (Objects.nonNull(entity)) {
			// 删除视频记录
			int i = eduVideoMapper.deleteById(id);
			// 删除云端资源
			if (i > 0) {
				aliyunVodService.deleteVideos(Collections.singletonList(entity.getVideoId()));
			}
			// 总课时-1
			Integer count = eduVideoMapper.selectCount(
					Wrappers.lambdaQuery(EduVideoEntity.class)
							.eq(EduVideoEntity::getCourseId, entity.getCourseId())
			);
			eduCourseMapper.update(null,
					Wrappers.lambdaUpdate(EduCourseEntity.class)
							.eq(EduCourseEntity::getId, entity.getCourseId())
							.set(EduCourseEntity::getLessonNum, count)
			);
			return RUtils.commonFailOrNot(i, "视频删除");
		}
		return RUtils.fail("视频不存在");
	}

	/**
	 * 获取视频播放凭证（学员）
	 */
	public R getVideoPlayAuth(int courseId, int videoId, String videoSourceId) {
		// 检查是否已登录
		if (!SessionUtils.checkMemberLogin(IPUtils.getRequest())) {
			return new R(RS.NOT_LOGIN.status(), "请登录后再操作");
		}
		// 检查课程是否免费
		EduCourseEntity course = eduCourseMapper.selectOne(
				Wrappers.lambdaQuery(EduCourseEntity.class)
						.select(EduCourseEntity::getPrice)
						.eq(EduCourseEntity::getId, courseId)
		);
		if (Objects.nonNull(course) && course.getPrice() <= 0) {
			String playAuth = aliyunVodService.getPlayAuth(videoSourceId);
			return RUtils.success("视频播放凭证", playAuth);
		}
		// 检查单个视频是否免费
		EduVideoEntity video = eduVideoMapper.selectOne(
				Wrappers.lambdaQuery(EduVideoEntity.class)
						.select(EduVideoEntity::getFree)
						.eq(EduVideoEntity::getId, videoId)
		);
		if (Objects.nonNull(video) && video.getFree()) {
			String playAuth = aliyunVodService.getPlayAuth(videoSourceId);
			return RUtils.success("视频播放凭证", playAuth);
		}
		// 检查是否已经订阅课程(付费视频)
		UctrMemberDetail member = SessionUtils.getMember(IPUtils.getRequest());
		Integer count = relCourseMemberMapper.selectCount(
				Wrappers.lambdaQuery(RelCourseMemberEntity.class)
						.eq(RelCourseMemberEntity::getMemberId, member.getId())
						.eq(RelCourseMemberEntity::getCourseId, courseId)
		);
		if (count < 1) {
			return RUtils.fail("请订阅该课程再点播");
		}
		String playAuth = aliyunVodService.getPlayAuth(videoSourceId);
		return RUtils.success("视频播放凭证", playAuth);
	}

	/**
	 * 转换成VO
	 */
	public List<EduVideoVO> covertToVO(List<EduVideoEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> (EduVideoVO) new EduVideoVO().convertFrom(e))
				.collect(Collectors.toList());
	}
}
