package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduChapterTmpVO;
import xyz.refrain.onlineedu.model.vo.teacher.EduChapterVO;
import xyz.refrain.onlineedu.service.EduChapterService;
import xyz.refrain.onlineedu.service.EduChapterTmpService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * 管理员端章节控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminEduChapterController")
@RequestMapping("/api/admin/chapter")
@Api(value = "管理员端章节控制器", tags = {"管理员端章节接口"})
public class EduChapterController {

	@Autowired
	private EduChapterService eduChapterService;

	@Autowired
	private EduChapterTmpService eduChapterTmpService;

	@GetMapping("/list/{courseId}")
	@ApiOperation("获取章节")
	public R list(@PathVariable("courseId") @Min(1) Integer courseId) {
		List<EduChapterVO> list = eduChapterService.listChapters(courseId);
		return RUtils.success("章节列表信息", list);
	}

	@GetMapping("/tmp/list/{courseId}")
	@ApiOperation("获取章节")
	public R listTmp(@PathVariable("courseId") @Min(1) Integer courseId) {
		List<EduChapterTmpVO> list = eduChapterTmpService.listChapters(courseId);
		return RUtils.success("章节列表信息", list);
	}


}
