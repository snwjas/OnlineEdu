package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.params.EduCourseSearchParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduCourseDetailVO;
import xyz.refrain.onlineedu.service.EduCourseService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;

/**
 * 后台课程控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminEduCourseController")
@RequestMapping("/api/admin/course")
@Api(value = "后台课程控制器", tags = {"后台课程接口"})
public class EduCourseController {

	@Autowired
	private EduCourseService eduCourseService;

	@GetMapping("/info/{id}")
	@ApiOperation("获取课程详细的信息")
	public R info(@PathVariable("id") @Min(1) Integer id) {
		return eduCourseService.getDetails(id);
	}

	@PostMapping("/list")
	@ApiOperation("搜索课程")
	public R list(@RequestBody @Valid EduCourseSearchParam param) {
		return eduCourseService.list(param);
	}

	@PostMapping("/update")
	@ApiOperation("修改课程信息")
	public R updateProfile(@Validated EduCourseDetailVO vo,
	                       @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		return eduCourseService.update(vo, file);
	}

	@PostMapping("/disable/{id}")
	@ApiOperation("下架课程")
	public R disable(@PathVariable("id") @Min(1) Integer id) {
		return eduCourseService.disable(id);
	}

	@PostMapping("/enable/{id}")
	@ApiOperation("上架课程")
	public R enable(@PathVariable("id") @Min(1) Integer id) {
		return eduCourseService.enable(id);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("删除课程")
	public R delete(@PathVariable("id") @Min(1) Integer id) {
		return eduCourseService.delete(id);
	}

	@PostMapping("/pass/{id}")
	@ApiOperation("通过审核")
	public R pass(@PathVariable("id") @Min(1) Integer id) {
		return eduCourseService.pass(id);
	}

	@PostMapping("/reject")
	@ApiOperation("驳回审核")
	public R turnDown(@RequestParam("id") @Min(1) Integer id,
	                  @RequestParam("remarks") @NotEmpty(message = "驳回备注不能为空") String remarks) {
		return eduCourseService.turnDown(id, remarks);
	}

	@PostMapping("/upload/pic")
	@ApiOperation("上传图片")
	public R pass(@RequestParam(value = "file") MultipartFile file) throws IOException {
		return eduCourseService.uploadPic(file);
	}
}
