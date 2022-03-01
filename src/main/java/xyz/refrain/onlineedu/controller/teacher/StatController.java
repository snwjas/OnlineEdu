package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.refrain.onlineedu.model.securtiy.EduTeacherDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.StatService;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 讲师端数据统计控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherStatController")
@RequestMapping("/api/teacher/stat")
@Api(value = "讲师端数据统计控制器", tags = {"讲师端数据统计接口"})
public class StatController {

	@Autowired
	private StatService statService;

	@GetMapping("/get/common")
	@ApiOperation("获取讲师数据统计")
	public R getCommon(HttpServletRequest request) {
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		return statService.getTchStat(teacher.getId());
	}

}
