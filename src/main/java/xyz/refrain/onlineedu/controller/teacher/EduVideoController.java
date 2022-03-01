package xyz.refrain.onlineedu.controller.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoVO;
import xyz.refrain.onlineedu.service.EduVideoService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 讲师端视频控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("TeacherEduVideoController")
@RequestMapping("/api/teacher/video")
@Api(value = "讲师端视频控制器", tags = {"讲师端视频接口"})
public class EduVideoController {

	@Autowired
	private EduVideoService eduVideoService;

	@GetMapping("/list/{chapterId}")
	@ApiOperation("获取章节视频")
	public R list(@PathVariable("chapterId") @Min(1) Integer chapterId) {
		List<EduVideoVO> list = eduVideoService.listVideos(chapterId);
		return RUtils.success("章节视频列表信息", list);
	}

	@PostMapping("/create")
	@ApiOperation("上传视频")
	public R create(@Min(1) @NotNull Integer courseId,
	                @Min(1) @NotNull Integer chapterId,
	                @RequestPart("file") MultipartFile file) {
		return eduVideoService.create(courseId, chapterId, file);
	}

	@PostMapping("/update")
	@ApiOperation("更新视频信息")
	public R update(@RequestBody @Validated(ValidGroupType.Update.class) EduVideoVO vo) {
		return eduVideoService.update(vo);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation("删除视频")
	public R delete(@PathVariable("id") @Min(1) Integer id) {
		return eduVideoService.delete(id);
	}

}
