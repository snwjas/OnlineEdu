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
import xyz.refrain.onlineedu.mapper.UctrMemberMapper;
import xyz.refrain.onlineedu.model.entity.UctrMemberEntity;
import xyz.refrain.onlineedu.model.enums.SexEnum;
import xyz.refrain.onlineedu.model.params.*;
import xyz.refrain.onlineedu.model.securtiy.UctrMemberDetail;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.app.UctrMemberDetailVO;
import xyz.refrain.onlineedu.model.vo.app.UctrMemberSimpleVO;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Edu Teacher Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class UctrMemberService {

	@Resource
	private UctrMemberMapper uctrMemberMapper;

	@Autowired
	private AliyunOssService aliyunOssService;

	/**
	 * 分页查询
	 */
	public R list(UctrMemberSearchParam param) {
		String nickname = param.getNickname();
		String mobile = param.getMobile();
		Boolean enable = param.getEnable();
		LocalDateTime beginCreate = param.getBeginCreate();
		LocalDateTime endCreate = param.getEndCreate();
		// 条件构造
		Page<UctrMemberEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<UctrMemberEntity> wrapper = Wrappers.lambdaQuery(UctrMemberEntity.class)
				.eq(Objects.nonNull(enable), UctrMemberEntity::getEnable, enable)
				.like(StringUtils.hasText(mobile), UctrMemberEntity::getMobile, mobile)
				.like(StringUtils.hasText(nickname), UctrMemberEntity::getNickname, nickname)
				.ge(Objects.nonNull(beginCreate), UctrMemberEntity::getCreateTime, beginCreate)
				.le(Objects.nonNull(endCreate), UctrMemberEntity::getCreateTime, endCreate);
		// 分页查询
		Page<UctrMemberEntity> entityPage = uctrMemberMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<UctrMemberSimpleVO> voPageResult = covertToPageResult(entityPage);
		// 返回
		return RUtils.success("学员列表信息", voPageResult);
	}

	/**
	 * 获取详细的信息
	 */
	public R getDetails(int userId) {
		UctrMemberEntity entity = uctrMemberMapper.selectById(userId);
		if (Objects.isNull(entity)) {
			return RUtils.fail("学员不存在");
		}
		UctrMemberDetailVO detailVO = new UctrMemberDetailVO().convertFrom(entity);
		return RUtils.success("学员详细信息", detailVO);
	}

	/**
	 * 登录
	 */
	public R login(LoginParam param) {
		UctrMemberEntity entity = uctrMemberMapper.selectOne(
				Wrappers.lambdaQuery(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getMobile, param.getUsername())
		);
		// 用户不存在
		if (Objects.isNull(entity)) {
			return RUtils.fail(RS.USERNAME_ERROR);
		}
		// 账户被禁用
		if (!entity.getEnable()) {
			return RUtils.fail(RS.ACCOUNT_DISABLED);
		}
		// 密码错误
		String encodedPassword = SessionUtils.encodePassword(param.getPassword());
		if (!encodedPassword.equals(entity.getPassword())) {
			return RUtils.fail(RS.PASSWORD_ERROR);
		}

		UctrMemberDetail userDetail = new UctrMemberDetail().convertFrom(entity);
		// 设置登录token
		String token = SessionUtils.generateToken(userDetail.getId());
		userDetail.setToken(token);
		// 保存用户信息到 redis 中
		SessionUtils.saveMember(userDetail);

		return RUtils.success("登录成功", new UctrMemberDetailVO().convertFrom(userDetail));
	}

	/**
	 * 注销登录
	 */
	public R logout() {
		boolean b = SessionUtils.removeMember(IPUtils.getRequest());
		return RUtils.commonFailOrNot(b ? 1 : 0, "登出");
	}

	/**
	 * 拉取用户信息
	 */
	public R info() {
		UctrMemberDetail detail = SessionUtils.getMember(IPUtils.getRequest());
		return RUtils.success("用户信息", new UctrMemberDetailVO().convertFrom(detail));
	}

	/**
	 * 注册
	 */
	public R register(RegisterParam param) {
		// 两次输入的密码不一致
		if (!param.getPassword().equals(param.getConfirmPassword())) {
			return RUtils.fail(RS.INCONSISTENT_PASSWORDS);
		}
		// 检查昵称是否存在
		if (isMobileExist(null, param.getUsername())) {
			return RUtils.fail("该手机已存在");
		}
		// 加密密码
		String encodePassword = SessionUtils.encodePassword(param.getPassword());
		UctrMemberEntity entity = new UctrMemberEntity();
		entity.setMobile(param.getUsername())
				.setPassword(encodePassword)
				.setNickname(param.getUsername());
		int i = uctrMemberMapper.insert(entity);
		return RUtils.commonFailOrNot(i, "账户注册");
	}

	/**
	 * 创建用户(管理员创建)
	 */
	public R create(UctrMemberDetail detail, MultipartFile file) throws IOException {
		// 检查昵称是否存在
		if (Objects.nonNull(detail.getNickname()) && isNicknameExist(null, detail.getNickname())) {
			return RUtils.fail("该昵称已存在");
		}
		// 检查昵称是否存在
		if (Objects.nonNull(detail.getMobile()) && isMobileExist(null, detail.getMobile())) {
			return RUtils.fail("该手机已存在");
		}
		// 头像
		if (Objects.nonNull(file)) {
			R r = updateAvatar(null, file);
			if (r.getStatus() != 200) {
				return r;
			}
			detail.setAvatar((String) r.getData());
		}
		// 如果密码为空，默认为654321
		String password = Objects.isNull(detail.getPassword()) ? "654321" : detail.getPassword();
		// 加密密码
		detail.setPassword(SessionUtils.encodePassword(password));
		// 设置账户可用
		detail.setEnable(true);
		// 执行插入
		UctrMemberEntity entity = detail.convertTo(new UctrMemberEntity());
		int i = uctrMemberMapper.insert(entity);
		return RUtils.commonFailOrNot(i, "创建学员");
	}

	/**
	 * 删除用户
	 */
	public R delete(int userId) {
		UctrMemberEntity entity = uctrMemberMapper.selectById(userId);
		// 删除图片云资源
		if (Objects.nonNull(entity)) {
			aliyunOssService.delete(entity.getAvatar());
		}
		int i = uctrMemberMapper.deleteById(userId);
		// 删除成功
		if (i > 0) {
			// 删除登录信息
			SessionUtils.removeMember(userId);
		}
		return RUtils.commonFailOrNot(i, "删除学员");
	}

	/**
	 * 禁用用户
	 */
	public R disable(int userId) {
		int i = uctrMemberMapper.update(null,
				Wrappers.lambdaUpdate(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getId, userId)
						.set(UctrMemberEntity::getEnable, false)
		);
		// 禁用成功
		if (i > 0) {
			// 删除登录信息
			SessionUtils.removeMember(userId);
			return RUtils.success("学员已禁用");
		}
		return RUtils.fail("学员不存在或禁用失败");
	}

	/**
	 * 启用用户
	 */
	public R enable(int userId) {
		int i = uctrMemberMapper.update(null,
				Wrappers.lambdaUpdate(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getId, userId)
						.set(UctrMemberEntity::getEnable, true)
		);
		if (i > 0) {
			return RUtils.success("学员已启用");
		}
		return RUtils.fail("学员不存在或启用失败");
	}

	/**
	 * 更新用户信息（针对已登录用户）
	 */
	public R updateProfile(UctrMemberDetailVO detail) {

		String nickname = detail.getNickname();
		// 检查昵称是否存在
		if (Objects.nonNull(nickname) && isNicknameExist(detail.getId(), nickname)) {
			return RUtils.fail("该昵称已存在");
		}

		String sign = detail.getSign();
		SexEnum sex = detail.getSex();
		Integer age = detail.getAge();

		int i = uctrMemberMapper.update(null,
				Wrappers.lambdaUpdate(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getId, detail.getId())
						.set(StringUtils.hasText(nickname), UctrMemberEntity::getNickname, nickname)
						.set(StringUtils.hasText(sign), UctrMemberEntity::getSign, sign)
						.set(Objects.nonNull(sex), UctrMemberEntity::getSex, sex)
						.set(Objects.nonNull(age), UctrMemberEntity::getAge, age)
		);
		if (i > 0) {
			// 更新session信息
			UctrMemberEntity entity = uctrMemberMapper.selectById(detail.getId());
			UctrMemberDetail member = new UctrMemberDetail().convertFrom(entity);
			String token = SessionUtils.getTokenFromRequest(IPUtils.getRequest());
			member.setToken(token);
			SessionUtils.saveMember(member);
			return RUtils.success("个人信息更新成功", member.convertTo(new UctrMemberDetailVO()));
		}
		return RUtils.fail("个人信息更新失败");
	}

	/**
	 * 更新用户信息(管理员权限，可直接更新手机号、邮箱等信息)
	 */
	public R updateProfileWithAdmin(UctrMemberDetailVO detail, MultipartFile file) throws IOException {
		String nickname = detail.getNickname();
		String mobile = detail.getMobile();
		// 检查昵称是否存在
		if (Objects.nonNull(nickname) && isNicknameExist(detail.getId(), nickname)) {
			return RUtils.fail("该昵称已存在");
		}
		// 检查昵称是否存在
		if (Objects.nonNull(mobile) && isMobileExist(detail.getId(), mobile)) {
			return RUtils.fail("该手机已存在");
		}

		// 头像
		String newAvatar = null;
		if (Objects.nonNull(file)) {
			R r = updateAvatar(detail.getId(), file);
			if (r.getStatus() != 200) {
				return r;
			}
			newAvatar = (String) r.getData();
		}

		String email = detail.getEmail();
		String sign = detail.getSign();
		SexEnum sex = detail.getSex();
		Integer age = detail.getAge();

		int i = uctrMemberMapper.update(null,
				Wrappers.lambdaUpdate(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getId, detail.getId())
						.set(StringUtils.hasText(mobile), UctrMemberEntity::getMobile, mobile)
						.set(StringUtils.hasText(email), UctrMemberEntity::getEmail, email)
						.set(StringUtils.hasText(nickname), UctrMemberEntity::getNickname, nickname)
						.set(StringUtils.hasText(sign), UctrMemberEntity::getSign, sign)
						.set(Objects.nonNull(sex), UctrMemberEntity::getSex, sex)
						.set(Objects.nonNull(age), UctrMemberEntity::getAge, age)
						.set(Objects.nonNull(newAvatar), UctrMemberEntity::getAvatar, newAvatar)
		);
		if (i > 0) {
			UctrMemberDetail userDetail = SessionUtils.getMember(detail.getId());
			// 更新session信息
			if (Objects.nonNull(userDetail)) {
				// 更新session信息
				UctrMemberEntity entity = uctrMemberMapper.selectById(detail.getId());
				UctrMemberDetail member = new UctrMemberDetail().convertFrom(entity);
				member.setToken(userDetail.getToken());
				SessionUtils.saveMember(member);
			}
		}
		return RUtils.commonFailOrNot(i, "学员信息更新");
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
		UctrMemberDetail detail = SessionUtils.getMember(IPUtils.getRequest());
		// 原密码错误
		String encodedPassword = SessionUtils.encodePassword(param.getOldPassword());
		if (!encodedPassword.equals(detail.getPassword())) {
			return RUtils.fail(RS.PASSWORD_ERROR);
		}
		// 更新数据库密码
		String newEncodedPassword = SessionUtils.encodePassword(param.getNewPassword());
		int i = uctrMemberMapper.update(null,
				Wrappers.lambdaUpdate(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getId, detail.getId())
						.set(UctrMemberEntity::getPassword, newEncodedPassword)
		);
		if (i > 0) {
			// 更新 Redis 中用户信息
			detail.setPassword(newEncodedPassword);
			SessionUtils.saveMember(detail);
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
		int i = uctrMemberMapper.update(null,
				Wrappers.lambdaUpdate(UctrMemberEntity.class)
						.eq(UctrMemberEntity::getId, userId)
						.set(UctrMemberEntity::getPassword, encodedPassword)
		);
		if (i > 0) {
			// 更新 session 信息
			UctrMemberDetail detail = SessionUtils.getMember(userId);
			if (Objects.nonNull(detail)) {
				detail.setPassword(encodedPassword);
				SessionUtils.saveMember(detail);
			}
		}
		return RUtils.commonFailOrNot(i, "密码修改");
	}

	/**
	 * 返回新头像url（针对管理员更新）
	 * 如果userId不为空且大于0，则删除目标用户头像,
	 */
	public R updateAvatar(Integer userId, MultipartFile file) throws IOException {
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
				if (Objects.nonNull(userId) && userId > 0) {
					UctrMemberEntity entity = uctrMemberMapper.selectById(userId);
					if (Objects.isNull(entity)) {
						return RUtils.fail("用户不存在");
					}
					// 删除原有头像
					aliyunOssService.delete(entity.getAvatar());
				}
				return RUtils.success("新头像URL", newAvatarUrl);
			}
		} else {
			return RUtils.fail("图片格式不支持");
		}
		return RUtils.fail("头像更新出错");
	}

	/**
	 * 更新头像（针对已经登录用户）
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
				UctrMemberDetail userDetail = SessionUtils.getMember(IPUtils.getRequest());
				aliyunOssService.delete(userDetail.getAvatar());
				// 更新新头像
				// 数据库更新
				int flag = uctrMemberMapper.update(null,
						Wrappers.lambdaUpdate(UctrMemberEntity.class)
								.eq(UctrMemberEntity::getId, userDetail.getId())
								.set(UctrMemberEntity::getAvatar, newAvatarUrl)
				);
				// session更新
				if (flag > 0) {
					userDetail.setAvatar(newAvatarUrl);
					SessionUtils.saveMember(userDetail);
					return RUtils.success("头像更新成功", newAvatarUrl);
				}
			}
		} else {
			return RUtils.fail("图片格式不支持");
		}
		return RUtils.fail("头像更新出错");
	}

	/**
	 * 检查手机号是否已存在，会忽略该userId的用户
	 *
	 * @return true, 已存在
	 */
	public boolean isMobileExist(Integer userId, String mobile) {
		Integer count = uctrMemberMapper.selectCount(
				Wrappers.lambdaQuery(UctrMemberEntity.class)
						.ne(Objects.nonNull(userId) && userId > 0, UctrMemberEntity::getId, userId)
						.eq(UctrMemberEntity::getMobile, mobile)
		);
		return Objects.nonNull(count) && count > 0;
	}

	/**
	 * 检查昵称是否已存在，会忽略该userId的用户
	 *
	 * @return true, 已存在
	 */
	public boolean isNicknameExist(Integer userId, String nickname) {
		Integer count = uctrMemberMapper.selectCount(
				Wrappers.lambdaQuery(UctrMemberEntity.class)
						.ne(Objects.nonNull(userId) && userId > 0, UctrMemberEntity::getId, userId)
						.eq(UctrMemberEntity::getNickname, nickname)
		);
		return Objects.nonNull(count) && count > 0;
	}

	/**
	 * 转换成分页数据
	 */
	public PageResult<UctrMemberSimpleVO> covertToPageResult(IPage<UctrMemberEntity> entityIPage) {
		List<UctrMemberSimpleVO> voList = entityIPage.getRecords().stream()
				.parallel()
				.map(e -> (UctrMemberSimpleVO) new UctrMemberSimpleVO().convertFrom(e))
				.collect(Collectors.toList());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}

}
