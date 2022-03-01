package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.annotation.AccessLimit;
import xyz.refrain.onlineedu.annotation.TimeCost;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.params.AclUserSearchParam;
import xyz.refrain.onlineedu.model.params.LoginParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordWithAdminParam;
import xyz.refrain.onlineedu.model.securtiy.AclUserDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.AclUserVO;
import xyz.refrain.onlineedu.service.AclUserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;

/**
 * 后台用户控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminAclUserController")
@RequestMapping("/api/admin/user")
@Api(value = "后台用户控制器", tags = {"后台用户接口"})
public class AclUserController {

	@Autowired
	private AclUserService aclUserService;

	@TimeCost
	@AccessLimit(maxCount = 3, seconds = 300)
	@PostMapping("/login")
	@ApiOperation("登录")
	public R login(@RequestBody @Valid LoginParam param) {
		return aclUserService.login(param);
	}

	@PostMapping("/logout")
	@ApiOperation("登出")
	public R logout() {
		return aclUserService.logout();
	}

	@GetMapping("/info")
	@ApiOperation("获取用户信息")
	public R info() {
		return aclUserService.info();
	}

	@PostMapping("/create")
	@ApiOperation("创建用户")
	public R create(@RequestBody @Validated({ValidGroupType.Save.class}) AclUserDetail aclUser) {
		return aclUserService.create(aclUser);
	}

	@PostMapping("/update/profile")
	@ApiOperation("修改用户信息")
	public R updateProfile(@RequestBody @Validated({ValidGroupType.Update.class}) AclUserVO aclUserVO) {
		return aclUserService.updateProfile(aclUserVO);
	}

	@PostMapping("/update/profile/admin")
	@ApiOperation("管理员权限修改用户信息")
	public R updateProfileWithAdmin(@RequestBody @Validated({ValidGroupType.Update.class}) AclUserVO aclUserVO) {
		return aclUserService.updateProfileWithAdmin(aclUserVO);
	}

	@PostMapping("/update/password")
	@ApiOperation("修改密码")
	public R updatePassword(@RequestBody @Valid UpdatePasswordParam param) {
		return aclUserService.updatePassword(param);
	}

	@PostMapping("/update/password/admin")
	@ApiOperation("管理员权限直接修改密码")
	public R updatePasswordWithAdmin(@RequestBody @Valid UpdatePasswordWithAdminParam param) {
		return aclUserService.updatePasswordWithAdmin(param);
	}

	@PostMapping("/update/avatar")
	@ApiOperation("修改头像")
	public R updateAvatar(@RequestPart("file") MultipartFile file) throws IOException {
		return aclUserService.updateAvatar(file);
	}

	@PostMapping("/disable/{userId}")
	@ApiOperation("禁用用户")
	public R disable(@PathVariable("userId") @Min(1) Integer userId) {
		return aclUserService.disable(userId);
	}

	@PostMapping("/enable/{userId}")
	@ApiOperation("启用用户")
	public R enable(@PathVariable("userId") @Min(1) Integer userId) {
		return aclUserService.enable(userId);
	}

	@PostMapping("/delete/{userId}")
	@ApiOperation("删除用户")
	public R delete(@PathVariable("userId") @Min(1) Integer userId) {
		return aclUserService.delete(userId);
	}

	@PostMapping("/list")
	@ApiOperation("搜索用户")
	public R list(@RequestBody @Valid AclUserSearchParam param) {
		return aclUserService.list(param);
	}

}
