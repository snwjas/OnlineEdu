package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.refrain.onlineedu.mapper.AclUserMapper;
import xyz.refrain.onlineedu.mapper.EduTeacherMapper;
import xyz.refrain.onlineedu.mapper.SysMessageMapper;
import xyz.refrain.onlineedu.model.entity.AclUserEntity;
import xyz.refrain.onlineedu.model.entity.EduTeacherEntity;
import xyz.refrain.onlineedu.model.entity.SysMessageEntity;
import xyz.refrain.onlineedu.model.enums.MessageRoleEnum;
import xyz.refrain.onlineedu.model.params.BasePageParam;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.common.SysMessageVO;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 消息service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class SysMessageService {

	@Resource
	private SysMessageMapper sysMessageMapper;

	@Resource
	private AclUserMapper aclUserMapper;

	@Resource
	private EduTeacherMapper eduTeacherMapper;

	/**
	 * 发送消息
	 *
	 * @param fromRole 发送者角色(管理员、讲师...)
	 * @param fromId   发送者Id
	 * @param toRole   接受者角色(教师、学员...)
	 * @param toId     接受者id
	 * @param content  消息内容
	 * @return 是否发送成功
	 */
	public boolean send(MessageRoleEnum fromRole, int fromId,
	                    MessageRoleEnum toRole, int toId, String content) {
		SysMessageEntity entity = new SysMessageEntity()
				.setFromRole(fromRole).setFromId(fromId)
				.setToRole(toRole).setToId(toId)
				.setContent(content).setHasRead(false);
		int i = sysMessageMapper.insert(entity);
		return i > 0;
	}

	/**
	 * 未读消息的数量，判断有没有新消息
	 */
	public int getNotReadMessageCount(MessageRoleEnum toRole, int toId) {
		return sysMessageMapper.selectCount(
				Wrappers.lambdaQuery(SysMessageEntity.class)
						.eq(SysMessageEntity::getToId, toId)
						.eq(SysMessageEntity::getToRole, toRole)
						.eq(SysMessageEntity::getHasRead, false)
		);
	}

	/**
	 * 分页获取消息列表
	 */
	public R list(MessageRoleEnum toRole, int toId, BasePageParam param) {
		// 条件构造
		Page<SysMessageEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<SysMessageEntity> wrapper = Wrappers.lambdaQuery(SysMessageEntity.class)
				.eq(SysMessageEntity::getToId, toId)
				.eq(SysMessageEntity::getToRole, toRole)
				.orderByDesc(SysMessageEntity::getId);
		// 分页查询
		Page<SysMessageEntity> entityPage = sysMessageMapper.selectPage(page, wrapper);
		// 数据转换
		List<SysMessageVO> voList = covertToVO(entityPage.getRecords());
		PageResult<SysMessageVO> pageResult = new PageResult<>(entityPage.getTotal(), voList);
		// 返回
		return RUtils.success("消息列表", pageResult);
	}

	/**
	 * 删除一条消息
	 */
	public R deleteOne(int messageId, MessageRoleEnum toRole, int toId) {
		int i = sysMessageMapper.delete(
				Wrappers.lambdaQuery(SysMessageEntity.class)
						.eq(SysMessageEntity::getId, messageId)
						.eq(SysMessageEntity::getToId, toId)
						.eq(SysMessageEntity::getToRole, toRole)
		);
		return RUtils.commonFailOrNot(i, "消息删除");
	}

	/**
	 * 标记所有消息为已读
	 */
	public int markAsRead(MessageRoleEnum toRole, int toId) {
		return sysMessageMapper.update(null,
				Wrappers.lambdaUpdate(SysMessageEntity.class)
						.eq(SysMessageEntity::getToId, toId)
						.eq(SysMessageEntity::getToRole, toRole)
						.set(SysMessageEntity::getHasRead, true)
		);
	}

	/**
	 * 删除全部消息
	 */
	public R deleteAll(MessageRoleEnum toRole, int toId) {
		int i = sysMessageMapper.delete(
				Wrappers.lambdaQuery(SysMessageEntity.class)
						.eq(SysMessageEntity::getToId, toId)
						.eq(SysMessageEntity::getToRole, toRole)
		);
		return RUtils.commonFailOrNot(i, "消息清空");
	}

	/**
	 * 获得发送者名称
	 */
	public String getFromName(MessageRoleEnum fromRole, Integer fromId) {
		if (Objects.isNull(fromRole) || Objects.isNull(fromId)) {
			return "";
		}
		if (MessageRoleEnum.FROM_ADMIN.equals(fromRole)) {
			AclUserEntity entity = aclUserMapper.selectOne(
					Wrappers.lambdaQuery(AclUserEntity.class)
							.select(AclUserEntity::getNickname)
							.eq(AclUserEntity::getId, fromId)
			);
			if (Objects.nonNull(entity)) {
				return entity.getNickname();
			}
		} else if (MessageRoleEnum.FROM_TEACHER.equals(fromRole)) {
			EduTeacherEntity entity = eduTeacherMapper.selectOne(
					Wrappers.lambdaQuery(EduTeacherEntity.class)
							.select(EduTeacherEntity::getName)
							.eq(EduTeacherEntity::getId, fromId)
			);
			if (Objects.nonNull(entity)) {
				return entity.getName();
			}
		}
		return "";
	}

	/**
	 * 转换成VO
	 */
	public List<SysMessageVO> covertToVO(List<SysMessageEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> {
					SysMessageVO vo = new SysMessageVO().convertFrom(e);
					vo.setFromName(getFromName(vo.getFromRole(), vo.getFromId()));
					return vo;
				})
				.collect(Collectors.toList());
	}


}
