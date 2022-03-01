package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.enums.CourseStatusEnum;
import xyz.refrain.onlineedu.model.params.EduCourseSearchParam;
import xyz.refrain.onlineedu.model.securtiy.EduTeacherDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduCourseDetailVO;
import xyz.refrain.onlineedu.service.EduCourseService;
import xyz.refrain.onlineedu.service.EduCourseTmpService;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 讲师端课程控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduCourseController")
@RequestMapping("/api/teacher/course")
@Api(value = "讲师端课程控制器", tags = {"讲师端课程接口"})
public class EduCourseController {

	@Autowired
	private EduCourseService eduCourseService;

	@Autowired
	private EduCourseTmpService eduCourseTmpService;

	@GetMapping("/info/{id}")
	@ApiOperation("获取课程详细的信息")
	public R info(@PathVariable("id") @Min(1) Integer id) {
		if (!isTeachersCourse(id)) {
			return RUtils.fail("参数不合法");
		}
		return eduCourseService.getDetails(id);
	}

	@PostMapping("/list")
	@ApiOperation("搜索课程")
	public R list(@RequestBody @Valid EduCourseSearchParam param, HttpServletRequest request) {
		// 限制讲师ID
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		param.setTeacherId(teacher.getId());
		return eduCourseService.list(param);
	}

	@GetMapping("/list/all")
	@ApiOperation("列出讲师所有的课程id与标题")
	public R listAll(HttpServletRequest request) {
		// 限制讲师ID
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		List<Map<String, Object>> result =
				eduCourseService.listTeacherCourseIdAndTitle(teacher.getId());
		return RUtils.success("讲师[" + teacher.getName() + "]所有的课程ID与标题", result);
	}

	@GetMapping("/draft")
	@ApiOperation("获取该讲师在草稿箱的课程")
	public R draft() {
		EduTeacherDetail teacher = SessionUtils.getTeacher(IPUtils.getRequest());
		return eduCourseService.getCourseInDraft(teacher.getId());
	}

	@PostMapping("/submit/{id}")
	@ApiOperation("提交审核课程")
	public R submit(@PathVariable("id") @Min(1) Integer id) {
		if (!isTeachersCourse(id)) {
			return RUtils.fail("参数不合法");
		}
		boolean b = eduCourseService.changeStatus(id, CourseStatusEnum.FIRST_AUDITING);
		return RUtils.commonFailOrNot(b ? 1 : 0, "提交审核");
	}

	@PostMapping("/create")
	@ApiOperation("创建课程")
	public R create(@Validated(ValidGroupType.Save.class) EduCourseDetailVO vo,
	                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		if (Objects.isNull(file)) {
			return RUtils.fail("课程封面不能为空");
		}
		// 设置讲师ID
		EduTeacherDetail teacher = SessionUtils.getTeacher(IPUtils.getRequest());
		vo.setTeacherId(teacher.getId());
		// 设置课程状态为草稿
		vo.setStatus(CourseStatusEnum.DRAFT);
		return eduCourseService.create(vo, file);
	}

	@PostMapping("/update")
	@ApiOperation("修改课程信息")
	public R update(@Validated(ValidGroupType.Update.class) EduCourseDetailVO vo,
	                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		if (!isTeachersCourse(vo.getId())) {
			return RUtils.fail("参数不合法");
		}
		// 讲师不可修改排序
		vo.setSort(null);
		return eduCourseTmpService.secondUpdate(vo, file);
	}

	@PostMapping("/update/{id}")
	@ApiOperation("修改课程章节视频信息")
	public R updateCV(@PathVariable("id") @Min(1) Integer courseId) {
		if (!isTeachersCourse(courseId)) {
			return RUtils.fail("参数不合法");
		}
		eduCourseTmpService.moveChapterAndVideoToTempFromOriginal(courseId);
		return RUtils.success("修改课程章节视频信息");
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("下架课程")
	public R delete(@PathVariable("id") @Min(1) Integer id) {
		if (!isTeachersCourse(id)) {
			return RUtils.fail("参数不合法");
		}
		return eduCourseService.disable(id);
	}

	@PostMapping("/upload/pic")
	@ApiOperation("上传图片")
	public R pass(@RequestParam(value = "file") MultipartFile file) throws IOException {
		return eduCourseService.uploadPic(file);
	}

	/**
	 * 判断是否是讲师的课程，目的是防止讲师篡改他人数据
	 */
	public boolean isTeachersCourse(int courseId) {
		// 限制讲师ID
		EduTeacherDetail teacher = SessionUtils.getTeacher(IPUtils.getRequest());
		Set<Integer> ids = eduCourseService.getTeacherCourseIds(teacher.getId());
		return ids.contains(courseId);
	}

}
