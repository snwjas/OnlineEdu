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
import xyz.refrain.onlineedu.mapper.EduVideoTmpMapper;
import xyz.refrain.onlineedu.model.entity.EduVideoTmpEntity;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoTmpVO;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.VideoUtil;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 视频 service（用于讲师二次修改课程）
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduVideoTmpService {

	@Resource
	private EduVideoTmpMapper eduVideoTmpMapper;

	@Autowired
	private AliyunVodService aliyunVodService;


	/**
	 * 获取根据章节ID视频数据
	 */
	public List<EduVideoTmpVO> listVideos(long chapterId) {
		List<EduVideoTmpEntity> entityList = eduVideoTmpMapper.selectList(
				Wrappers.lambdaQuery(EduVideoTmpEntity.class)
						.eq(EduVideoTmpEntity::getChapterId, chapterId)
						.orderByAsc(EduVideoTmpEntity::getSort)
						.orderByAsc(EduVideoTmpEntity::getTitle)
		);
		if (CollectionUtils.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		return covertToVO(entityList);
	}

	/**
	 * 新建视频(上传视频)
	 */
	public R create(int courseId, Long chapterId, MultipartFile file) {
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
		EduVideoTmpEntity entity = new EduVideoTmpEntity()
				.setCourseId(courseId)
				.setChapterId(chapterId)
				.setTitle(FileNameUtil.mainName(file.getOriginalFilename()))
				.setVideoId(videoId)
				.setPlayCount(0)
				.setDuration(VideoUtil.ReadVideoTimeMs(file))
				.setSort(0)
				.setSize(file.getSize())
				.setFree(false);
		eduVideoTmpMapper.insert(entity);
		// 返回
		String msg = StrUtil.format("视频文件[{}]上传成功", file.getOriginalFilename());
		return RUtils.success(msg, entity);
	}

	/**
	 * 更新视频信息
	 */
	public R update(EduVideoTmpVO vo) {
		String title = vo.getTitle();
		Integer sort = vo.getSort();
		Boolean free = vo.getFree();
		int i = eduVideoTmpMapper.update(null,
				Wrappers.lambdaUpdate(EduVideoTmpEntity.class)
						.eq(EduVideoTmpEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), EduVideoTmpEntity::getTitle, title)
						.set(Objects.nonNull(sort), EduVideoTmpEntity::getSort, sort)
						.set(Objects.nonNull(free), EduVideoTmpEntity::getFree, free)
		);
		return RUtils.commonFailOrNot(i, "视频信息更新");
	}

	/**
	 * 删除视频（不能删除原来的视频数据）
	 */
	public R delete(long id) {
		EduVideoTmpEntity entity = eduVideoTmpMapper.selectOne(
				Wrappers.lambdaQuery(EduVideoTmpEntity.class)
						.select(EduVideoTmpEntity::getOid, EduVideoTmpEntity::getVideoId)
						.eq(EduVideoTmpEntity::getId, id)
		);
		if (Objects.nonNull(entity)) {
			// 删除视频记录
			int i = eduVideoTmpMapper.deleteById(id);
			// 删除云端资源(判断删除的是否为原来的视频数据)
			if (i > 0 && (Objects.isNull(entity.getOid()) || entity.getOid() == 0)) {
				aliyunVodService.deleteVideos(Collections.singletonList(entity.getVideoId()));
			}
			return RUtils.commonFailOrNot(i, "视频删除");
		}
		return RUtils.fail("视频不存在");
	}

	/**
	 * 转换成VO
	 */
	public List<EduVideoTmpVO> covertToVO(List<EduVideoTmpEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> (EduVideoTmpVO) new EduVideoTmpVO().convertFrom(e))
				.collect(Collectors.toList());
	}
}
