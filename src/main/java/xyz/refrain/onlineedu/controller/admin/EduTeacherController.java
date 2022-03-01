package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;
import xyz.refrain.onlineedu.model.params.EduTeacherSearchParam;
import xyz.refrain.onlineedu.model.params.UpdatePasswordWithAdminParam;
import xyz.refrain.onlineedu.model.securtiy.EduTeacherDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduTeacherDetailVO;
import xyz.refrain.onlineedu.service.EduTeacherService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.Objects;

/**
 * 后台讲师控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminEduTeacherController")
@RequestMapping("/api/admin/teacher")
@Api(value = "后台讲师控制器", tags = {"后台讲师接口"})
public class EduTeacherController {

	@Autowired
	private EduTeacherService eduTeacherService;

	@GetMapping("/info/{userId}")
	@ApiOperation("获取讲师详细的信息")
	public R info(@PathVariable("userId") @Min(1) Integer userId) {
		return eduTeacherService.getDetails(userId);
	}

	@PostMapping("/list")
	@ApiOperation("搜索讲师")
	public R list(@RequestBody @Valid EduTeacherSearchParam param) {
		return eduTeacherService.list(param);
	}

	@PostMapping("/create")
	@ApiOperation("创建讲师")
	public R create(@Validated EduTeacherDetail detail,
	                @RequestPart(value = "file", required = false) MultipartFile file,
	                @RequestPart(value = "resume", required = false) MultipartFile resume) throws IOException {
		if (Objects.isNull(file)) {
			return RUtils.fail("头像不能为空");
		}
		return eduTeacherService.create(detail, file, resume, TeacherStatusEnum.PASS);
	}

	@PostMapping("/update/profile")
	@ApiOperation("修改讲师信息")
	public R updateProfile(@Validated EduTeacherDetailVO detailVO,
	                       @RequestPart(value = "file", required = false) MultipartFile file,
	                       @RequestPart(value = "resume", required = false) MultipartFile resume) throws IOException {
		return eduTeacherService.updateProfile(detailVO, file, resume);
	}

	@PostMapping("/update/password")
	@ApiOperation("管理员权限直接修改密码")
	public R updatePasswordWithAdmin(@RequestBody @Valid UpdatePasswordWithAdminParam param
	) {
		return eduTeacherService.updatePasswordWithAdmin(param);
	}

	@PostMapping("/disable/{userId}")
	@ApiOperation("禁用讲师")
	public R disable(@PathVariable("userId") @Min(1) Integer userId) {
		return eduTeacherService.disable(userId);
	}

	@PostMapping("/enable/{userId}")
	@ApiOperation("启用讲师")
	public R enable(@PathVariable("userId") @Min(1) Integer userId) {
		return eduTeacherService.enable(userId);
	}

	@PostMapping("/delete/{userId}")
	@ApiOperation("删除讲师")
	public R delete(@PathVariable("userId") @Min(1) Integer userId) {
		return eduTeacherService.delete(userId);
	}

	@PostMapping("/pass/{userId}")
	@ApiOperation("通过审核")
	public R pass(@PathVariable("userId") @Min(1) Integer userId) {
		return eduTeacherService.pass(userId);
	}

}
