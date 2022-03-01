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
import xyz.refrain.onlineedu.mapper.EduTeacherMapper;
import xyz.refrain.onlineedu.model.entity.EduTeacherEntity;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;
import xyz.refrain.onlineedu.model.params.EduTeacherSearchParam;
import xyz.refrain.onlineedu.model.params.LoginParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordWithAdminParam;
import xyz.refrain.onlineedu.model.securtiy.AclUserDetail;
import xyz.refrain.onlineedu.model.securtiy.EduTeacherDetail;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduTeacherDetailVO;
import xyz.refrain.onlineedu.model.vo.admin.EduTeacherSimpleVO;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.LambdaTypeUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Edu Teacher Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduTeacherService {

	@Resource
	private EduTeacherMapper eduTeacherMapper;

	@Autowired
	private AliyunOssService aliyunOssService;

	/**
	 * 分页查询
	 */
	public R list(EduTeacherSearchParam param) {
		String name = param.getName();
		String mobile = param.getMobile();
		Boolean enable = param.getEnable();
		TeacherStatusEnum status = param.getStatus();

		// 条件构造
		Page<EduTeacherEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<EduTeacherEntity> wrapper = Wrappers.lambdaQuery(EduTeacherEntity.class)
				.in(Objects.nonNull(status), EduTeacherEntity::getStatus, status)
				.eq(Objects.nonNull(enable), EduTeacherEntity::getEnable, enable)
				.like(StringUtils.hasText(mobile), EduTeacherEntity::getMobile, mobile)
				.like(StringUtils.hasText(name), EduTeacherEntity::getName, name)
				.orderByAsc(EduTeacherEntity::getSort);
		// 分页查询
		Page<EduTeacherEntity> entityPage = eduTeacherMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<EduTeacherSimpleVO> voPageResult = covertToPageResult(entityPage);
		// 返回
		return RUtils.success("讲师列表信息", voPageResult);
	}

	/**
	 * 获取详细的信息
	 */
	public R getDetails(int userId) {
		EduTeacherEntity entity = eduTeacherMapper.selectById(userId);
		if (Objects.isNull(entity)) {
			return RUtils.fail("讲师不存在");
		}
		EduTeacherDetailVO detailVO = new EduTeacherDetailVO().convertFrom(entity);
		return RUtils.success("讲师详细信息", detailVO);
	}

	/**
	 * 前端获取讲师信息
	 */
	public R getTeacherForApp(int teacherId) {
		EduTeacherEntity entity = eduTeacherMapper.selectOne(
				Wrappers.lambdaQuery(EduTeacherEntity.class)
						.select(EduTeacherEntity::getId, EduTeacherEntity::getName, EduTeacherEntity::getAvatar,
								EduTeacherEntity::getEmail, EduTeacherEntity::getIntro)
						.eq(EduTeacherEntity::getId, teacherId)
		);
		HashMap<String, Object> map = new HashMap<>(8);
		if (Objects.nonNull(entity)) {
			map.put("id", entity.getId());
			map.put(LambdaTypeUtils.getColumnName(EduTeacherEntity::getName), entity.getName());
			map.put(LambdaTypeUtils.getColumnName(EduTeacherEntity::getAvatar), entity.getAvatar());
			map.put(LambdaTypeUtils.getColumnName(EduTeacherEntity::getEmail), entity.getEmail());
			map.put(LambdaTypeUtils.getColumnName(EduTeacherEntity::getIntro), entity.getIntro());
		}
		return RUtils.success("讲师信息", map);
	}

	/**
	 * 登录
	 */
	public R login(LoginParam param) {
		EduTeacherEntity entity = eduTeacherMapper.selectOne(
				Wrappers.lambdaQuery(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getMobile, param.getUsername())
		);
		// 用户不存在
		if (Objects.isNull(entity)) {
			return RUtils.fail(RS.USERNAME_ERROR);
		}
		// 账户被禁用
		if (!entity.getEnable()) {
			return RUtils.fail(RS.ACCOUNT_DISABLED);
		}
		if (!TeacherStatusEnum.PASS.equals(entity.getStatus())) {
			return RUtils.fail("账户正在审核中");
		}
		// 密码错误
		String encodedPassword = SessionUtils.encodePassword(param.getPassword());
		if (!encodedPassword.equals(entity.getPassword())) {
			return RUtils.fail(RS.PASSWORD_ERROR);
		}

		EduTeacherDetail userDetail = new EduTeacherDetail().convertFrom(entity);
		// 设置登录token
		String token = SessionUtils.generateToken(userDetail.getId());
		userDetail.setToken(token);
		// 保存用户信息到 redis 中
		SessionUtils.saveTeacher(userDetail);

		return RUtils.success("登录成功", new EduTeacherDetailVO().convertFrom(userDetail));
	}

	/**
	 * 注销登录
	 */
	public R logout() {
		boolean b = SessionUtils.removeTeacher(IPUtils.getRequest());
		return RUtils.commonFailOrNot(b ? 1 : 0, "登出");
	}

	/**
	 * 拉取用户信息
	 */
	public R info() {
		EduTeacherDetail detail = SessionUtils.getTeacher(IPUtils.getRequest());
		return RUtils.success("用户信息", new EduTeacherDetailVO().convertFrom(detail));
	}

	/**
	 * 创建用户
	 */
	public R create(EduTeacherDetail detail, MultipartFile file, MultipartFile resume, TeacherStatusEnum status) throws IOException {
		// 检查昵称是否存在
		if (Objects.nonNull(detail.getName()) && isNameExist(null, detail.getName())) {
			return RUtils.fail("该名称已存在");
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
		// 简历
		if (Objects.nonNull(resume)) {
			R r = updateResume(null, resume);
			if (r.getStatus() != 200) {
				return r;
			}
			detail.setResume((String) r.getData());
		}
		// 如果密码为空，默认为654321
		String password = Objects.isNull(detail.getPassword()) ? "654321" : detail.getPassword();
		// 加密密码
		detail.setPassword(SessionUtils.encodePassword(password));
		// 设置账户可用
		detail.setEnable(true);
		// 设置账户状态
		detail.setStatus(status);
		// 执行插入
		EduTeacherEntity entity = detail.convertTo(new EduTeacherEntity());
		int i = eduTeacherMapper.insert(entity);
		return RUtils.commonFailOrNot(i, "创建讲师");
	}

	/**
	 * 删除用户
	 */
	public R delete(int userId) {
		EduTeacherEntity entity = eduTeacherMapper.selectById(userId);
		// 删除图片云资源
		if (Objects.nonNull(entity)) {
			aliyunOssService.delete(entity.getAvatar());
		}

		int i = eduTeacherMapper.deleteById(userId);
		// 删除成功
		if (i > 0) {
			// 删除登录信息
			SessionUtils.removeTeacher(userId);
		}
		return RUtils.commonFailOrNot(i, "删除讲师");
	}

	/**
	 * 禁用用户
	 */
	public R disable(int userId) {
		int i = eduTeacherMapper.update(null,
				Wrappers.lambdaUpdate(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getId, userId)
						.set(EduTeacherEntity::getEnable, false)
		);
		// 禁用成功
		if (i > 0) {
			// 删除登录信息
			SessionUtils.removeTeacher(userId);
			return RUtils.success("讲师已禁用");
		}
		return RUtils.fail("讲师不存在或禁用失败");
	}

	/**
	 * 启用用户
	 */
	public R enable(int userId) {
		int i = eduTeacherMapper.update(null,
				Wrappers.lambdaUpdate(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getId, userId)
						.set(EduTeacherEntity::getEnable, true)
		);
		if (i > 0) {
			return RUtils.success("讲师已启用");
		}
		return RUtils.fail("讲师不存在或启用失败");
	}

	/**
	 * 通过讲师的审核
	 */
	public R pass(int userId) {
		int i = eduTeacherMapper.update(null,
				Wrappers.lambdaUpdate(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getId, userId)
						.set(EduTeacherEntity::getStatus, TeacherStatusEnum.PASS)
		);
		if (i > 0) {
			return RUtils.success("该讲师已通过审核");
		}
		return RUtils.fail("讲师不存在或审核失败");
	}

	/**
	 * 更新用户信息(只能管理员更新，讲师端没有更新信息操作)
	 */
	public R updateProfile(EduTeacherDetailVO detail, MultipartFile file, MultipartFile resume) throws IOException {
		// 检查昵称是否存在
		if (Objects.nonNull(detail.getName()) && isNameExist(detail.getId(), detail.getName())) {
			return RUtils.fail("该名称已存在");
		}
		// 检查昵称是否存在
		if (Objects.nonNull(detail.getMobile()) && isMobileExist(detail.getId(), detail.getMobile())) {
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
		// 简历
		String newResume = null;
		if (Objects.nonNull(resume)) {
			R r = updateResume(detail.getId(), resume);
			if (r.getStatus() != 200) {
				return r;
			}
			newResume = (String) r.getData();
		}

		String mobile = detail.getMobile();
		String email = detail.getEmail();
		String name = detail.getName();
		String intro = detail.getIntro();
		Integer sort = detail.getSort();
		Integer division = detail.getDivision();
		TeacherStatusEnum status = detail.getStatus();

		int i = eduTeacherMapper.update(null,
				Wrappers.lambdaUpdate(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getId, detail.getId())
						.set(StringUtils.hasText(mobile), EduTeacherEntity::getMobile, mobile)
						.set(StringUtils.hasText(email), EduTeacherEntity::getEmail, email)
						.set(StringUtils.hasText(name), EduTeacherEntity::getName, name)
						.set(StringUtils.hasText(intro), EduTeacherEntity::getIntro, intro)
						.set(Objects.nonNull(sort), EduTeacherEntity::getSort, sort)
						.set(Objects.nonNull(division), EduTeacherEntity::getDivision, division)
						.set(Objects.nonNull(status), EduTeacherEntity::getStatus, status)
						.set(Objects.nonNull(newAvatar), EduTeacherEntity::getAvatar, newAvatar)
						.set(Objects.nonNull(newResume), EduTeacherEntity::getResume, newResume)
		);
		if (i > 0) {
			EduTeacherDetail teacher = SessionUtils.getTeacher(detail.getId());
			// 更新session信息
			if (Objects.nonNull(teacher)) {
				EduTeacherEntity entity = eduTeacherMapper.selectById(detail.getId());
				AclUserDetail userDetail = new AclUserDetail().convertFrom(entity);
				userDetail.setToken(teacher.getToken());
				SessionUtils.saveAclUser(userDetail);
			}
			return RUtils.success("讲师信息更新成功");
		}
		return RUtils.commonFailOrNot(i, "讲师信息更新");
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
		EduTeacherDetail detail = SessionUtils.getTeacher(IPUtils.getRequest());
		// 原密码错误
		String encodedPassword = SessionUtils.encodePassword(param.getOldPassword());
		if (!encodedPassword.equals(detail.getPassword())) {
			return RUtils.fail(RS.PASSWORD_ERROR);
		}
		// 更新数据库密码
		String newEncodedPassword = SessionUtils.encodePassword(param.getNewPassword());
		int i = eduTeacherMapper.update(null,
				Wrappers.lambdaUpdate(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getId, detail.getId())
						.set(EduTeacherEntity::getPassword, newEncodedPassword)
		);
		if (i > 0) {
			// 更新 Redis 中用户信息
			detail.setPassword(newEncodedPassword);
			SessionUtils.saveTeacher(detail);
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
		int i = eduTeacherMapper.update(null,
				Wrappers.lambdaUpdate(EduTeacherEntity.class)
						.eq(EduTeacherEntity::getId, userId)
						.set(EduTeacherEntity::getPassword, encodedPassword)
		);
		if (i > 0) {
			// 更新 session 信息
			EduTeacherDetail detail = SessionUtils.getTeacher(userId);
			if (Objects.nonNull(detail)) {
				detail.setPassword(encodedPassword);
				SessionUtils.saveTeacher(detail);
			}
		}
		return RUtils.commonFailOrNot(i, "密码修改");
	}

	/**
	 * 返回新头像url
	 * 如果userId不为空且大于0，则删除目标用户头像,
	 */
	public R updateAvatar(Integer userId, MultipartFile file) throws IOException {
		// 图片大小限制在 1MB
		if (file.getSize() > 1048576L) {
			return RUtils.fail("图片文件过大");
		}
		String type = FileTypeUtil.getType(file.getInputStream());
		type = Objects.nonNull(type) ? type.toLowerCase(Locale.ROOT) : "";
		// 判断是否目标格式图片文件
		if ("jpg".equals(type) || "jpeg".equals(type) || "png".equals(type)) {
			String newAvatarUrl = aliyunOssService.upload(file);
			// 头像上传成功
			if (StringUtils.hasText(newAvatarUrl)) {
				if (Objects.nonNull(userId) && userId > 0) {
					EduTeacherEntity entity = eduTeacherMapper.selectById(userId);
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
	 * 返回新简历url
	 * 如果userId不为空且大于0，则删除目标用户简历,
	 */
	public R updateResume(Integer userId, MultipartFile file) throws IOException {
		// 简历大小限制在 5MB
		if (file.getSize() > 5242880L) {
			return RUtils.fail("简历文件过大");
		}
		String type = FileTypeUtil.getType(file.getInputStream());
		type = Objects.nonNull(type) ? type.toLowerCase(Locale.ROOT) : "";
		// 判断是否目标格式图片文件
		if ("jpg".equals(type) || "jpeg".equals(type) || "png".equals(type) || "pdf".equals(type)) {
			String newResumeUrl = aliyunOssService.upload(file);
			// 简历上传成功
			if (StringUtils.hasText(newResumeUrl)) {
				if (Objects.nonNull(userId) && userId > 0) {
					EduTeacherEntity entity = eduTeacherMapper.selectById(userId);
					if (Objects.isNull(entity)) {
						return RUtils.fail("用户不存在");
					}
					// 删除原有简历
					aliyunOssService.delete(entity.getResume());
				}
				return RUtils.success("简历URL", newResumeUrl);
			}
		} else {
			return RUtils.fail("简历格式不支持");
		}
		return RUtils.fail("简历更新出错");
	}

	/**
	 * 检查手机号是否已存在
	 *
	 * @return true, 已存在
	 */
	public boolean isMobileExist(Integer userId, String mobile) {
		Integer count = eduTeacherMapper.selectCount(
				Wrappers.lambdaQuery(EduTeacherEntity.class)
						.ne(Objects.nonNull(userId) && userId > 0, EduTeacherEntity::getId, userId)
						.eq(EduTeacherEntity::getMobile, mobile)
		);
		return Objects.nonNull(count) && count > 0;
	}

	/**
	 * 检查用户名是否已存在
	 *
	 * @return true, 已存在
	 */
	public boolean isNameExist(Integer userId, String username) {
		Integer count = eduTeacherMapper.selectCount(
				Wrappers.lambdaQuery(EduTeacherEntity.class)
						.ne(Objects.nonNull(userId) && userId > 0, EduTeacherEntity::getId, userId)
						.eq(EduTeacherEntity::getName, username)
		);
		return Objects.nonNull(count) && count > 0;
	}


	/**
	 * 转换成分页数据
	 */
	public PageResult<EduTeacherSimpleVO> covertToPageResult(IPage<EduTeacherEntity> entityIPage) {
		List<EduTeacherSimpleVO> voList = entityIPage.getRecords().stream()
				.parallel()
				.map(e -> (EduTeacherSimpleVO) new EduTeacherSimpleVO().convertFrom(e))
				.collect(Collectors.toList());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}

}
