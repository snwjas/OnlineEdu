package xyz.refrain.onlineedu.service;

import cn.hutool.core.io.FileTypeUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.mapper.AclUserMapper;
import xyz.refrain.onlineedu.model.entity.AclUserEntity;
import xyz.refrain.onlineedu.model.params.AclUserSearchParam;
import xyz.refrain.onlineedu.model.params.LoginParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordWithAdminParam;
import xyz.refrain.onlineedu.model.securtiy.AclUserDetail;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.AclUserVO;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AclUser Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class AclUserService {

	@Resource
	private AclUserMapper aclUserMapper;

	@Autowired
	private AliyunOssService aliyunOssService;


	/**
	 * 登录
	 */
	public R login(LoginParam param) {
		AclUserEntity aclUserEntity = aclUserMapper.selectOne(
				Wrappers.lambdaQuery(AclUserEntity.class)
						.eq(AclUserEntity::getUsername, param.getUsername())
		);
		// 用户不存在
		if (Objects.isNull(aclUserEntity)) {
			return RUtils.fail(RS.USERNAME_ERROR);
		}
		// 密码错误
		String encodedPassword = SessionUtils.encodePassword(param.getPassword());
		if (!encodedPassword.equals(aclUserEntity.getPassword())) {
			return RUtils.fail(RS.PASSWORD_ERROR);
		}
		// 账户被禁用
		if (!aclUserEntity.getEnable()) {
			return RUtils.fail(RS.ACCOUNT_DISABLED);
		}

		AclUserDetail aclUserDetail = new AclUserDetail().convertFrom(aclUserEntity);
		// 设置登录token
		String token = SessionUtils.generateToken(aclUserDetail.getId());
		aclUserDetail.setToken(token);
		// 保存用户信息到 redis 中
		SessionUtils.saveAclUser(aclUserDetail);

		return RUtils.success("登录成功", new AclUserVO().convertFrom(aclUserDetail));
	}

	/**
	 * 注销登录
	 */
	public R logout() {
		boolean b = SessionUtils.removeAclUser(IPUtils.getRequest());
		return RUtils.commonFailOrNot(b ? 1 : 0, "登出");
	}

	/**
	 * 拉取用户信息
	 */
	public R info() {
		AclUserDetail aclUser = SessionUtils.getAclUser(IPUtils.getRequest());
		return RUtils.success("用户信息", new AclUserVO().convertFrom(aclUser));
	}

	/**
	 * 创建用户
	 */
	public R create(AclUserDetail aclUser) {
		// 检查用户名是否存在
		if (isUsernameExist(null, aclUser.getUsername())) {
			return RUtils.fail("用户名已存在");
		}
		// 加密密码
		aclUser.setPassword(SessionUtils.encodePassword(aclUser.getPassword()));
		// 设置账户可用
		aclUser.setEnable(true);
		// 执行插入
		AclUserEntity aclUserEntity = aclUser.convertTo(new AclUserEntity());
		int i = aclUserMapper.insert(aclUserEntity);
		return RUtils.commonFailOrNot(i, "创建用户");
	}

	/**
	 * 删除用户
	 */
	public R delete(int userId) {
		// userId 为 1 的为默认管理员，不能删除
		if (userId == 1) {
			return RUtils.fail("默认管理员无法删除");
		}
		AclUserEntity entity = aclUserMapper.selectById(userId);
		// 删除图片云资源
		if (Objects.nonNull(entity)) {
			aliyunOssService.delete(entity.getAvatar());
		}
		int i = aclUserMapper.deleteById(userId);
		// 删除成功
		if (i > 0) {
			// 删除登录信息
			SessionUtils.removeAclUser(userId);
		}
		return RUtils.commonFailOrNot(i, "删除用户");
	}

	/**
	 * 禁用用户
	 */
	public R disable(int aclUserId) {
		// userId 为 1 的为默认管理员，默认启用
		if (aclUserId == 1) {
			return RUtils.fail("默认管理员无法禁用");
		}
		int i = aclUserMapper.update(null,
				Wrappers.lambdaUpdate(AclUserEntity.class)
						.eq(AclUserEntity::getId, aclUserId)
						.set(AclUserEntity::getEnable, false)
		);
		// 禁用成功
		if (i > 0) {
			// 删除登录信息
			SessionUtils.removeAclUser(aclUserId);
			return RUtils.success("用户已禁用");
		}
		return RUtils.fail("用户不存在或禁用失败");
	}

	/**
	 * 启用用户
	 */
	public R enable(int aclUserId) {
		int i = aclUserMapper.update(null,
				Wrappers.lambdaUpdate(AclUserEntity.class)
						.eq(AclUserEntity::getId, aclUserId)
						.set(AclUserEntity::getEnable, true)
		);
		if (i > 0) {
			return RUtils.success("用户已启用");
		}
		return RUtils.fail("用户不存在或启用失败");
	}

	/**
	 * 更新用户信息（针对已登录用户）
	 */
	public R updateProfile(AclUserVO aclUserVO) {
		String nickname = aclUserVO.getNickname();
		String sign = aclUserVO.getSign();
		int i = aclUserMapper.update(null,
				Wrappers.lambdaUpdate(AclUserEntity.class)
						.eq(AclUserEntity::getId, aclUserVO.getId())
						.set(StringUtils.hasText(nickname), AclUserEntity::getNickname, nickname)
						.set(StringUtils.hasText(sign), AclUserEntity::getSign, sign)
		);
		if (i > 0) {
			// 更新session信息
			AclUserEntity aclUserEntity = aclUserMapper.selectById(aclUserVO.getId());
			AclUserDetail userDetail = new AclUserDetail().convertFrom(aclUserEntity);
			String token = SessionUtils.getTokenFromRequest(IPUtils.getRequest());
			userDetail.setToken(token);
			SessionUtils.saveAclUser(userDetail);
			return RUtils.success("用户信息更新成功", userDetail.convertTo(new AclUserDetail()));
		}
		return RUtils.fail("用户信息更新失败");
	}


	/**
	 * 更新用户信息(管理员权限，可更新用户名)
	 */
	public R updateProfileWithAdmin(AclUserVO aclUserVO) {
		String username = aclUserVO.getUsername();
		// 检查用户名是否存在
		if (Objects.nonNull(username) && isUsernameExist(aclUserVO.getId(), username)) {
			return RUtils.fail("用户名已存在");
		}
		Integer roleId = aclUserVO.getRoleId();
		String nickname = aclUserVO.getNickname();
		String mark = aclUserVO.getMark();
		int i = aclUserMapper.update(null,
				Wrappers.lambdaUpdate(AclUserEntity.class)
						.eq(AclUserEntity::getId, aclUserVO.getId())
						.set(StringUtils.hasText(username), AclUserEntity::getUsername, username)
						.set(Objects.nonNull(roleId), AclUserEntity::getRoleId, roleId)
						.set(StringUtils.hasText(nickname), AclUserEntity::getNickname, nickname)
						.set(StringUtils.hasText(mark), AclUserEntity::getMark, mark)
		);
		if (i > 0) {
			AclUserDetail userDetail = SessionUtils.getAclUser(aclUserVO.getId());
			// 更新session信息
			if (Objects.nonNull(userDetail)) {
				// 更新session信息
				AclUserEntity aclUserEntity = aclUserMapper.selectById(aclUserVO.getId());
				AclUserDetail aclUser = new AclUserDetail().convertFrom(aclUserEntity);
				aclUser.setToken(userDetail.getToken());
				SessionUtils.saveAclUser(aclUser);
			}
		}
		return RUtils.commonFailOrNot(i, "用户信息更新");
	}


	/**
	 * 更新密码
	 */
	public R updatePassword(UpdatePasswordParam param) {
		String newPassword = param.getNewPassword();
		String confirmNewPassword = param.getConfirmNewPassword();
		// 两次输入的密码不一致
		if (!newPassword.equals(confirmNewPassword)) {
			return RUtils.fail(RS.INCONSISTENT_PASSWORDS);
		}
		AclUserDetail aclUser = SessionUtils.getAclUser(IPUtils.getRequest());
		// 原密码错误
		String encodedPassword = SessionUtils.encodePassword(param.getOldPassword());
		if (!encodedPassword.equals(aclUser.getPassword())) {
			return RUtils.fail(RS.PASSWORD_ERROR);
		}
		// 更新数据库密码
		String newEncodedPassword = SessionUtils.encodePassword(param.getNewPassword());
		int i = aclUserMapper.update(null,
				Wrappers.lambdaUpdate(AclUserEntity.class)
						.eq(AclUserEntity::getId, aclUser.getId())
						.set(AclUserEntity::getPassword, newEncodedPassword)
		);
		if (i > 0) {
			// 更新 Redis 中用户信息
			aclUser.setPassword(newEncodedPassword);
			SessionUtils.saveAclUser(aclUser);
		}

		return RUtils.commonFailOrNot(i, "密码更新");
	}

	/**
	 * 更新密码（管理员对所有用户重置密码）
	 */
	public R updatePasswordWithAdmin(UpdatePasswordWithAdminParam param) {
		Integer userId = param.getUserId();
		String newPassword = param.getNewPassword();

		String encodedPassword = SessionUtils.encodePassword(newPassword);
		int i = aclUserMapper.update(null,
				Wrappers.lambdaUpdate(AclUserEntity.class)
						.eq(AclUserEntity::getId, userId)
						.set(AclUserEntity::getPassword, encodedPassword)
		);
		if (i > 0) {
			// 更新 session 信息
			AclUserDetail aclUser = SessionUtils.getAclUser(userId);
			if (Objects.nonNull(aclUser)) {
				aclUser.setPassword(encodedPassword);
				SessionUtils.saveAclUser(aclUser);
			}
		}
		return RUtils.commonFailOrNot(i, "密码修改");
	}

	/**
	 * 更新头像
	 */
	public R updateAvatar(MultipartFile file) throws IOException {
		// 图片大小限制在 1MB
		if (file.getSize() > 1048576L) {
			return RUtils.fail("图片文件过大");
		}
		String type = FileTypeUtil.getType(file.getInputStream());
		// 判断是否目标格式图片文件
		if ("jpg".equals(type) || "jpeg".equals(type) || "png".equals(type)) {
			String newAvatarUrl = aliyunOssService.upload(file);
			// 头像上传成功
			if (StringUtils.hasText(newAvatarUrl)) {
				// 删除原有头像
				AclUserDetail aclUser = SessionUtils.getAclUser(IPUtils.getRequest());
				aliyunOssService.delete(aclUser.getAvatar());
				// 更新新头像
				// 数据库更新
				int flag = aclUserMapper.update(null,
						Wrappers.lambdaUpdate(AclUserEntity.class)
								.eq(AclUserEntity::getId, aclUser.getId())
								.set(AclUserEntity::getAvatar, newAvatarUrl)
				);
				// session更新
				if (flag > 0) {
					aclUser.setAvatar(newAvatarUrl);
					SessionUtils.saveAclUser(aclUser);
					return RUtils.success("头像更新成功", newAvatarUrl);
				}
			}
		} else {
			return RUtils.fail("图片格式不支持");
		}
		return RUtils.fail("头像更新出错");
	}

	/**
	 * 分页查询
	 */
	public R list(AclUserSearchParam param) {
		String username = param.getUsername();
		Boolean enable = param.getEnable();
		Integer roleId = param.getRoleId();
		// 条件构造
		Page<AclUserEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<AclUserEntity> wrapper = Wrappers.lambdaQuery(AclUserEntity.class)
				.eq(Objects.nonNull(enable), AclUserEntity::getEnable, enable)
				.eq(Objects.nonNull(roleId), AclUserEntity::getRoleId, roleId)
				.like(StringUtils.hasText(username), AclUserEntity::getUsername, username);
		// 分页查询
		Page<AclUserEntity> entityPage = aclUserMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<AclUserVO> voPageResult = covertToPageResult(entityPage);
		// 返回
		return RUtils.success("系统用户列表信息", voPageResult);
	}

	/**
	 * 检查用户名是否已存在
	 *
	 * @return true, 已存在
	 */
	public boolean isUsernameExist(Integer userId, String username) {
		Integer count = aclUserMapper.selectCount(
				Wrappers.lambdaQuery(AclUserEntity.class)
						.ne(Objects.nonNull(userId) && userId > 0, AclUserEntity::getId, userId)
						.eq(AclUserEntity::getUsername, username)
		);
		return Objects.nonNull(count) && count > 0;
	}

	/**
	 * 转换成分页数据
	 */
	public PageResult<AclUserVO> covertToPageResult(IPage<AclUserEntity> entityIPage) {
		List<AclUserVO> voList = entityIPage.getRecords().stream()
				.parallel()
				.map(e -> (AclUserVO) new AclUserVO().convertFrom(e))
				.collect(Collectors.toList());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}


}
