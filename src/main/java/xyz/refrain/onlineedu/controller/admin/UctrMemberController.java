package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.params.UctrMemberSearchParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordWithAdminParam;
import xyz.refrain.onlineedu.model.securtiy.UctrMemberDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.app.UctrMemberDetailVO;
import xyz.refrain.onlineedu.service.UctrMemberService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;

/**
 * 后台学员控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminUctrMemberController")
@RequestMapping("/api/admin/member")
@Api(value = "后台学员控制器", tags = {"后台学员接口"})
public class UctrMemberController {

	@Autowired
	private UctrMemberService uctrMemberService;

	@GetMapping("/info/{userId}")
	@ApiOperation("获取学员详细的信息")
	public R info(@PathVariable("userId") @Min(1) Integer userId) {
		return uctrMemberService.getDetails(userId);
	}

	@PostMapping("/list")
	@ApiOperation("搜索学员")
	public R list(@RequestBody @Valid UctrMemberSearchParam param) {
		return uctrMemberService.list(param);
	}

	@PostMapping("/create")
	@ApiOperation("创建学员")
	public R create(@Validated UctrMemberDetail detail,
	                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		return uctrMemberService.create(detail, file);
	}

	@PostMapping("/update/profile")
	@ApiOperation("修改学员信息")
	public R updateProfile(@Validated UctrMemberDetailVO detailVO,
	                       @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		return uctrMemberService.updateProfileWithAdmin(detailVO, file);
	}

	@PostMapping("/update/password")
	@ApiOperation("管理员权限直接修改密码")
	public R updatePasswordWithAdmin(@RequestBody @Valid UpdatePasswordWithAdminParam param) {
		return uctrMemberService.updatePasswordWithAdmin(param);
	}

	@PostMapping("/disable/{userId}")
	@ApiOperation("禁用学员")
	public R disable(@PathVariable("userId") @Min(1) Integer userId) {
		return uctrMemberService.disable(userId);
	}

	@PostMapping("/enable/{userId}")
	@ApiOperation("启用学员")
	public R enable(@PathVariable("userId") @Min(1) Integer userId) {
		return uctrMemberService.enable(userId);
	}

	@PostMapping("/delete/{userId}")
	@ApiOperation("删除学员")
	public R delete(@PathVariable("userId") @Min(1) Integer userId) {
		return uctrMemberService.delete(userId);
	}


}
