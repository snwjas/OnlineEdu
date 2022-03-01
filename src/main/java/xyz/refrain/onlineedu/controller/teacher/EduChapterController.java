package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduChapterVO;
import xyz.refrain.onlineedu.service.EduChapterService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * 讲师端章节控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduChapterController")
@RequestMapping("/api/teacher/chapter")
@Api(value = "讲师端章节控制器", tags = {"讲师端章节接口"})
public class EduChapterController {

	@Autowired
	private EduChapterService eduChapterService;

	@GetMapping("/list/{courseId}")
	@ApiOperation("获取章节")
	public R list(@PathVariable("courseId") @Min(1) Integer courseId) {
		List<EduChapterVO> list = eduChapterService.listChapters(courseId);
		return RUtils.success("章节列表信息", list);
	}

	@PostMapping("/create")
	@ApiOperation("创建章节")
	public R create(@RequestBody @Validated(ValidGroupType.Save.class) EduChapterVO vo) {
		return eduChapterService.create(vo);
	}

	@PostMapping("/update")
	@ApiOperation("更新章节信息")
	public R update(@RequestBody @Validated(ValidGroupType.Update.class) EduChapterVO vo) {
		return eduChapterService.update(vo);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("删除章节")
	public R delete(@PathVariable("id") @Min(1) Integer id) {
		return eduChapterService.delete(id);
	}

}
