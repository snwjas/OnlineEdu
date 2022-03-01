package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.refrain.onlineedu.annotation.AccessLimit;
import xyz.refrain.onlineedu.annotation.TimeCost;
import xyz.refrain.onlineedu.model.params.LoginParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.EduTeacherService;

import javax.validation.Valid;

/**
 * 讲师端讲师控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduTeacherController")
@RequestMapping("/api/teacher/user")
@Api(value = "讲师端讲师控制器", tags = {"讲师端讲师接口"})
public class EduTeacherController {

	@Autowired
	private EduTeacherService eduTeacherService;

	@TimeCost
	@AccessLimit(maxCount = 3, seconds = 300)
	@PostMapping("/login")
	@ApiOperation("登录")
	public R login(@RequestBody @Valid LoginParam param) {
		return eduTeacherService.login(param);
	}

	@PostMapping("/logout")
	@ApiOperation("登出")
	public R logout() {
		return eduTeacherService.logout();
	}

	@GetMapping("/info")
	@ApiOperation("获取登录用户信息")
	public R info() {
		return eduTeacherService.info();
	}

	@PostMapping("/update/password")
	@ApiOperation("修改密码")
	public R updatePassword(@RequestBody @Valid UpdatePasswordParam param) {
		return eduTeacherService.updatePassword(param);
	}

}
