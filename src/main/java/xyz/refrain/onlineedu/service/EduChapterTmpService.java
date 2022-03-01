package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.refrain.onlineedu.mapper.EduChapterTmpMapper;
import xyz.refrain.onlineedu.mapper.EduVideoTmpMapper;
import xyz.refrain.onlineedu.model.entity.EduChapterEntity;
import xyz.refrain.onlineedu.model.entity.EduChapterTmpEntity;
import xyz.refrain.onlineedu.model.entity.EduVideoTmpEntity;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduChapterTmpVO;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 章节 service（讲师二次修改课程章节）
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduChapterTmpService {

	@Resource
	private EduChapterTmpMapper eduChapterTmpMapper;

	@Resource
	private EduVideoTmpMapper eduVideoTmpMapper;

	@Autowired
	private AliyunVodService aliyunVodService;


	/**
	 * 获取章节数据
	 */
	public List<EduChapterTmpVO> listChapters(int courseId) {
		List<EduChapterTmpEntity> entityList = eduChapterTmpMapper.selectList(
				Wrappers.lambdaQuery(EduChapterTmpEntity.class)
						.eq(EduChapterTmpEntity::getCourseId, courseId)
						.orderByAsc(EduChapterTmpEntity::getSort)
						.orderByAsc(EduChapterTmpEntity::getTitle)
		);
		if (CollectionUtils.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		return covertToVO(entityList);
	}

	/**
	 * 新建章节
	 */
	public R create(EduChapterTmpVO vo) {
		EduChapterTmpEntity entity = vo.convertTo(new EduChapterTmpEntity());
		eduChapterTmpMapper.insert(entity);
		return RUtils.success("章节创建成功", entity);
	}

	/**
	 * 更新章节信息
	 */
	public R update(EduChapterTmpVO vo) {
		String title = vo.getTitle();
		Integer sort = vo.getSort();
		int i = eduChapterTmpMapper.update(null,
				Wrappers.lambdaUpdate(EduChapterTmpEntity.class)
						.eq(EduChapterTmpEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), EduChapterTmpEntity::getTitle, title)
						.set(Objects.nonNull(sort), EduChapterTmpEntity::getSort, sort)
		);
		return RUtils.commonFailOrNot(i, "章节信息更新");
	}

	/**
	 * 删除章节（如果章节下有视频，将全部删除）
	 */
	public R delete(long chapterId) {
		EduChapterTmpEntity chapter = eduChapterTmpMapper.selectById(chapterId);
		int i = eduChapterTmpMapper.deleteById(chapterId);
		if (i > 0) {
			// 获取章节下的视频信息
			List<EduVideoTmpEntity> videoEntityList = eduVideoTmpMapper.selectList(
					Wrappers.lambdaQuery(EduVideoTmpEntity.class)
							.select(EduVideoTmpEntity::getOid, EduVideoTmpEntity::getVideoId)
							.eq(EduVideoTmpEntity::getChapterId, chapterId)
			);
			// 待删除的视频和云端资源ID列表
			ArrayList<Long> videoId = new ArrayList<>();
			ArrayList<String> videoSourceId = new ArrayList<>();
			// 检查是否为新章节
			// 是，是原有课程没有的新章节
			if (Objects.isNull(chapter.getOid()) || chapter.getOid() == 0) {
				videoEntityList.forEach(e -> {
					videoId.add(e.getId());
					videoSourceId.add(e.getVideoId());
				});
			} else { // 不是新章节（原有课程下面存在的章节）
				// 过滤
				videoEntityList.forEach(e -> {
					videoId.add(e.getId());
					if (Objects.isNull(e.getOid()) || e.getOid() == 0) {
						videoSourceId.add(e.getVideoId());
					}
				});
			}
			// 删除视频记录
			if (CollectionUtils.isNotEmpty(videoId)) {
				eduVideoTmpMapper.deleteBatchIds(videoId);
			}
			// 删除云端视频资源
			if (CollectionUtils.isEmpty(videoSourceId)) {
				aliyunVodService.deleteVideos(videoSourceId);
			}
		}
		return RUtils.commonFailOrNot(i, "章节删除");
	}

	/**
	 * 转换成VO
	 */
	public List<EduChapterTmpVO> covertToVO(List<EduChapterTmpEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> (EduChapterTmpVO) new EduChapterTmpVO().convertFrom(e))
				.collect(Collectors.toList());
	}

}
