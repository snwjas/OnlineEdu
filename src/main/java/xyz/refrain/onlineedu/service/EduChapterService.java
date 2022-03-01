package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.refrain.onlineedu.mapper.EduChapterMapper;
import xyz.refrain.onlineedu.mapper.EduVideoMapper;
import xyz.refrain.onlineedu.model.entity.EduChapterEntity;
import xyz.refrain.onlineedu.model.entity.EduVideoEntity;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduChapterVO;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 章节 service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduChapterService {

	@Resource
	private EduChapterMapper eduChapterMapper;

	@Resource
	private EduVideoMapper eduVideoMapper;

	@Autowired
	private AliyunVodService aliyunVodService;

	/**
	 * 获取章节数据
	 */
	public List<EduChapterVO> listChapters(int courseId) {
		List<EduChapterEntity> entityList = eduChapterMapper.selectList(
				Wrappers.lambdaQuery(EduChapterEntity.class)
						.eq(EduChapterEntity::getCourseId, courseId)
						.orderByAsc(EduChapterEntity::getSort)
						.orderByAsc(EduChapterEntity::getTitle)
		);
		if (CollectionUtils.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		return covertToVO(entityList);
	}

	/**
	 * 新建章节
	 */
	public R create(EduChapterVO vo) {
		EduChapterEntity entity = vo.convertTo(new EduChapterEntity());
		eduChapterMapper.insert(entity);
		return RUtils.success("章节创建成功", entity);
	}

	/**
	 * 更新章节信息
	 */
	public R update(EduChapterVO vo) {
		String title = vo.getTitle();
		Integer sort = vo.getSort();
		int i = eduChapterMapper.update(null,
				Wrappers.lambdaUpdate(EduChapterEntity.class)
						.eq(EduChapterEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), EduChapterEntity::getTitle, title)
						.set(Objects.nonNull(sort), EduChapterEntity::getSort, sort)
		);
		return RUtils.commonFailOrNot(i, "章节信息更新");
	}

	/**
	 * 删除章节（如果章节下有视频，将全部删除）
	 */
	public R delete(int chapterId) {
		int i = eduChapterMapper.deleteById(chapterId);
		if (i > 0) {
			// 删除视频与文件
			List<EduVideoEntity> videoEntityList = eduVideoMapper.selectList(
					Wrappers.lambdaQuery(EduVideoEntity.class)
							.select(EduVideoEntity::getId, EduVideoEntity::getVideoId)
							.eq(EduVideoEntity::getChapterId, chapterId)
			);
			ArrayList<Integer> videoId = new ArrayList<>();
			ArrayList<String> videoSourceId = new ArrayList<>();
			videoEntityList.forEach(e -> {
				videoId.add(e.getId());
				videoSourceId.add(e.getVideoId());
			});
			if (CollectionUtils.isNotEmpty(videoId)) {
				eduVideoMapper.deleteBatchIds(videoId);
			}
			if (CollectionUtils.isEmpty(videoSourceId)) {
				aliyunVodService.deleteVideos(videoSourceId);
			}
		}
		return RUtils.commonFailOrNot(i, "章节删除");
	}

	/**
	 * 转换成VO
	 */
	public List<EduChapterVO> covertToVO(List<EduChapterEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> (EduChapterVO) new EduChapterVO().convertFrom(e))
				.collect(Collectors.toList());
	}

}
