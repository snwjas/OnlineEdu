package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.params.EduSubjectSearchParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduSubjectDetailVO;
import xyz.refrain.onlineedu.service.EduSubjectService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 课程科目(分类)控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminEduSubjectController")
@RequestMapping("/api/admin/subject")
@Api(value = "后台课程科目(分类)控制器控制器", tags = {"后台课程科目(分类)控制器接口"})
public class EduSubjectController {

	@Autowired
	private EduSubjectService eduSubjectService;

	@GetMapping("/get")
	@ApiOperation("获取所有分类")
	public R get() {
		return eduSubjectService.get();
	}

	@PostMapping("/list")
	@ApiOperation("搜索分类")
	public R list(@RequestBody @Valid EduSubjectSearchParam param) {
		return eduSubjectService.list(param);
	}

	@PostMapping("/create")
	@ApiOperation("创建分类")
	public R create(@RequestBody @Validated(ValidGroupType.Save.class) EduSubjectDetailVO vo) {
		return eduSubjectService.create(vo);
	}

	@PostMapping("/update")
	@ApiOperation("修改分类信息")
	public R updateProfile(@RequestBody @Validated(ValidGroupType.Update.class) EduSubjectDetailVO vo) {
		return eduSubjectService.update(vo);
	}

	@PostMapping("/refresh")
	@ApiOperation("刷新分类缓存")
	public R refresh() {
		return eduSubjectService.refresh();
	}

	@PostMapping("/disable/{id}")
	@ApiOperation("禁用分类")
	public R disable(@PathVariable("id") @Min(1) Integer id) {
		return eduSubjectService.disable(id);
	}

	@PostMapping("/enable/{id}")
	@ApiOperation("启用分类")
	public R enable(@PathVariable("id") @Min(1) Integer id) {
		return eduSubjectService.enable(id);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("删除分类")
	public R delete(@PathVariable("id") @Min(1) Integer id) {
		return eduSubjectService.delete(id);
	}

}
