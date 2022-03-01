package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.params.EduCommentSearchParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduCommentVO;
import xyz.refrain.onlineedu.service.EduCommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 讲师端评论控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduCommentController")
@RequestMapping("/api/teacher/comment")
@Api(value = "讲师端评论控制器", tags = {"讲师端评论接口"})
public class EduCommentController {

	@Autowired
	private EduCommentService eduCommentService;

	@PostMapping("/list")
	@ApiOperation("搜索评论")
	public R list(@RequestBody @Valid EduCommentSearchParam param) {
		return eduCommentService.list(param);
	}

	@PostMapping("/create")
	@ApiOperation("创建评论")
	public R create(@RequestBody @Validated(ValidGroupType.Save.class) EduCommentVO vo) {
		return eduCommentService.create(vo);
	}

	@PostMapping("/update")
	@ApiOperation("更新评论信息")
	public R updateProfile(@RequestBody @Validated(ValidGroupType.Update.class) EduCommentVO vo) {
		return eduCommentService.update(vo);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("删除评论")
	public R delete(@PathVariable("id") @Min(1) Integer id) {
		return eduCommentService.delete(id);
	}

}
