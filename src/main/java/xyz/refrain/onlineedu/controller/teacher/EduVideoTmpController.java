package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoTmpVO;
import xyz.refrain.onlineedu.service.EduVideoTmpService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 讲师端视频控制器（用于讲师二次二次修改课程）
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduVideoTmpController")
@RequestMapping("/api/teacher/video/tmp")
@Api(value = "讲师端视频控制器（二次修改）", tags = {"讲师端视频接口（二次修改）"})
public class EduVideoTmpController {

	@Autowired
	private EduVideoTmpService eduVideoTmpService;

	@GetMapping("/list/{chapterId}")
	@ApiOperation("获取章节视频")
	public R list(@PathVariable("chapterId") @Min(1) Long chapterId) {
		List<EduVideoTmpVO> list = eduVideoTmpService.listVideos(chapterId);
		return RUtils.success("章节视频列表信息", list);
	}

	@PostMapping("/create")
	@ApiOperation("上传视频")
	public R create(@Min(1) @NotNull Integer courseId,
	                @Min(1) @NotNull Long chapterId,
	                @RequestPart("file") MultipartFile file) {
		return eduVideoTmpService.create(courseId, chapterId, file);
	}

	@PostMapping("/update")
	@ApiOperation("更新视频信息")
	public R update(@RequestBody @Validated(ValidGroupType.Update.class) EduVideoTmpVO vo) {
		return eduVideoTmpService.update(vo);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("删除视频")
	public R delete(@PathVariable("id") @Min(1) Long id) {
		return eduVideoTmpService.delete(id);
	}

}
