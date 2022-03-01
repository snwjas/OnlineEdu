package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.mapper.EduCommentMapper;
import xyz.refrain.onlineedu.mapper.EduCourseMapper;
import xyz.refrain.onlineedu.mapper.UctrMemberMapper;
import xyz.refrain.onlineedu.model.entity.EduCommentEntity;
import xyz.refrain.onlineedu.model.entity.EduCourseEntity;
import xyz.refrain.onlineedu.model.entity.UctrMemberEntity;
import xyz.refrain.onlineedu.model.params.EduCommentSearchParam;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduCommentVO;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 评论 Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduCommentService {

	@Resource
	private EduCommentMapper eduCommentMapper;

	@Resource
	private EduCourseMapper eduCourseMapper;

	@Resource
	private UctrMemberMapper uctrMemberMapper;

	/**
	 * 分页查询评论
	 */
	public R list(EduCommentSearchParam param) {
		Integer courseId = param.getCourseId();
		Boolean status = param.getStatus();
		// 条件构造
		Page<EduCommentEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<EduCommentEntity> wrapper = Wrappers.lambdaQuery(EduCommentEntity.class)
				.eq(Objects.nonNull(courseId), EduCommentEntity::getCourseId, courseId)
				.eq(Objects.nonNull(status), EduCommentEntity::getStatus, status);
		// 分页查询
		Page<EduCommentEntity> entityPage = eduCommentMapper.selectPage(page, wrapper);
		// 数据转换
		List<EduCommentVO> voList = covertToListVO(entityPage.getRecords());
		PageResult<EduCommentVO> pageResult = new PageResult<>(entityPage.getTotal(), voList);
		// 返回
		return RUtils.success("评论列表", pageResult);
	}

	/**
	 * 发表评论
	 */
	public R create(EduCommentVO vo) {
		EduCommentEntity entity = vo.convertTo(new EduCommentEntity());
		int i = eduCommentMapper.insert(entity);
		return RUtils.commonFailOrNot(i, "评论发表");
	}

	/**
	 * 更新评论
	 */
	public R update(EduCommentVO vo) {
		String content = vo.getContent();
		Double mark = vo.getMark();
		Boolean status = vo.getStatus();
		int i = eduCommentMapper.update(null,
				Wrappers.lambdaUpdate(EduCommentEntity.class)
						.eq(EduCommentEntity::getId, vo.getId())
						.set(StringUtils.hasText(content), EduCommentEntity::getContent, content)
						.set(Objects.nonNull(mark), EduCommentEntity::getMark, mark)
						.set(Objects.nonNull(status), EduCommentEntity::getStatus, status)
		);
		return RUtils.commonFailOrNot(i, "评论更新");
	}

	// 删除评论
	public R delete(int id) {
		int i = eduCommentMapper.deleteById(id);
		return RUtils.commonFailOrNot(i, "评论删除");
	}

	/**
	 * 发表评论（学员）
	 */
	public R publish(EduCommentVO vo) {
		// 检查是否已登录
		if (!SessionUtils.checkMemberLogin(IPUtils.getRequest())) {
			return new R(RS.NOT_LOGIN.status(), "请登录后再操作");
		}
		// 检查是否已经发表过了
		Integer count = eduCommentMapper.selectCount(
				Wrappers.lambdaQuery(EduCommentEntity.class)
						.eq(EduCommentEntity::getMemberId, vo.getMemberId())
						.eq(EduCommentEntity::getCourseId, vo.getCourseId())
		);
		if (count > 0) {
			return RUtils.fail("不能重复发布课程评价");
		}
		vo.setStatus(false);
		return create(vo);
	}

	public List<EduCommentVO> covertToListVO(List<EduCommentEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> {
					EduCommentVO vo = new EduCommentVO().convertFrom(e);
					if (Objects.nonNull(vo.getCourseId())) {
						EduCourseEntity courseEntity = eduCourseMapper.selectById(vo.getCourseId());
						vo.setCourseName(Objects.nonNull(courseEntity) ? courseEntity.getTitle() : "");
					}
					if (Objects.nonNull(vo.getMemberId())) {
						UctrMemberEntity memberEntity = uctrMemberMapper.selectById(vo.getMemberId());
						vo.setMemberName(Objects.nonNull(memberEntity) ? memberEntity.getNickname() : "");
						vo.setMemberAvatar(Objects.nonNull(memberEntity) ? memberEntity.getAvatar() : "");
					}
					return vo;
				})
				.collect(Collectors.toList());
	}


}
