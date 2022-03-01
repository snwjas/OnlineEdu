package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.refrain.onlineedu.model.enums.MessageRoleEnum;
import xyz.refrain.onlineedu.model.params.BasePageParam;
import xyz.refrain.onlineedu.model.securtiy.EduTeacherDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.SysMessageService;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 讲师端消息控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherSysMessageController")
@RequestMapping("/api/teacher/message")
@Api(value = "讲师端消息控制器", tags = {"讲师端消息接口"})
public class SysMessageController {

	@Autowired
	private SysMessageService sysMessageService;

	@PostMapping("/list")
	@ApiOperation("获取所有消息")
	public R list(@RequestBody @Validated BasePageParam param, HttpServletRequest request) {
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		return sysMessageService.list(MessageRoleEnum.TO_TEACHER, teacher.getId(), param);
	}

	@GetMapping("/check")
	@ApiOperation("检查是否有未读消息")
	public R check(HttpServletRequest request) {
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		int count = sysMessageService.getNotReadMessageCount(
				MessageRoleEnum.TO_TEACHER, teacher.getId());
		return RUtils.success("未读消息数", count);
	}

	@GetMapping("/read")
	@ApiOperation("标记消息为已读")
	public R markAsRead(HttpServletRequest request) {
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		sysMessageService.markAsRead(MessageRoleEnum.TO_TEACHER, teacher.getId());
		return RUtils.succeed();
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation("删除单条消息")
	public R delete(@PathVariable("id") Integer id, HttpServletRequest request) {
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		return sysMessageService.deleteOne(id, MessageRoleEnum.TO_TEACHER, teacher.getId());
	}

	@DeleteMapping("/clear")
	@ApiOperation("清空所有消息")
	public R delete(HttpServletRequest request) {
		EduTeacherDetail teacher = SessionUtils.getTeacher(request);
		return sysMessageService.deleteAll(MessageRoleEnum.TO_TEACHER, teacher.getId());
	}

}
