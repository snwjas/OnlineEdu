package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.EduSubjectService;

/**
 * 讲师端分类控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduSubjectController")
@RequestMapping("/api/teacher/subject")
@Api(value = "讲师端视频控制器", tags = {"讲师端视频接口"})
public class EduSubjectController {

	@Autowired
	private EduSubjectService eduSubjectService;

	@GetMapping("/get")
	@ApiOperation("获取所有分类")
	public R get() {
		return eduSubjectService.get();
	}

}
