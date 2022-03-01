package xyz.refrain.onlineedu.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.annotation.AccessLimit;
import xyz.refrain.onlineedu.annotation.TimeCost;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.constant.StatConstant;
import xyz.refrain.onlineedu.model.params.LoginParam;
import xyz.refrain.onlineedu.model.params.RegisterParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.app.UctrMemberDetailVO;
import xyz.refrain.onlineedu.service.UctrMemberService;
import xyz.refrain.onlineedu.utils.RedisUtils;

import javax.validation.Valid;
import java.io.IOException;

/**
 * 前台学员控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AppUctrMemberController")
@RequestMapping("/api/app/member")
@Api(value = "前台学员控制器", tags = {"前台学员接口"})
public class UctrMemberController {

	@Autowired
	private UctrMemberService uctrMemberService;

	@AccessLimit(maxCount = 1, seconds = 120)
	@PostMapping("/register")
	@ApiOperation("注册")
	public R register(@RequestBody @Valid RegisterParam param) {
		R r = uctrMemberService.register(param);

		// 统计每天注册数量
		if (RS.SUCCESS.status() == r.getStatus()) {
			RedisUtils.incr(StatConstant.REGISTER_COUNT, 1);
		}

		return r;
	}

	@TimeCost
	@AccessLimit(maxCount = 3, seconds = 300)
	@PostMapping("/login")
	@ApiOperation("登录")
	public R login(@RequestBody @Valid LoginParam param) {
		return uctrMemberService.login(param);
	}

	@PostMapping("/logout")
	@ApiOperation("登出")
	public R logout() {
		return uctrMemberService.logout();
	}

	@GetMapping("/info")
	@ApiOperation("获取登录用户信息")
	public R info() {
		R r = uctrMemberService.info();

		// 统计活跃人数
		UctrMemberDetailVO member = (UctrMemberDetailVO) (r.getData());
		RedisUtils.set(StatConstant.LOGIN_COUNT + member.getId(), null);

		return r;
	}

	@PostMapping("/update/password")
	@ApiOperation("修改密码")
	public R updatePassword(@RequestBody @Valid UpdatePasswordParam param) {
		return uctrMemberService.updatePassword(param);
	}

	@PostMapping("/update/profile")
	@ApiOperation("修改学员信息")
	public R updateProfile(@RequestBody @Validated UctrMemberDetailVO detailVO) {
		return uctrMemberService.updateProfile(detailVO);
	}

	@PostMapping("/update/avatar")
	@ApiOperation("修改头像")
	public R updateAvatar(@RequestPart("file") MultipartFile file) throws IOException {
		return uctrMemberService.updateAvatar(file);
	}

}
