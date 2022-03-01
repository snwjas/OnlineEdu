package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoTmpVO;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoVO;
import xyz.refrain.onlineedu.service.AliyunVodService;
import xyz.refrain.onlineedu.service.EduVideoService;
import xyz.refrain.onlineedu.service.EduVideoTmpService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 管理员端视频控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminEduVideoController")
@RequestMapping("/api/admin/video")
@Api(value = "管理员端视频控制器", tags = {"管理员端视频接口"})
public class EduVideoController {

	@Autowired
	private EduVideoService eduVideoService;

	@Autowired
	private EduVideoTmpService eduVideoTmpService;

	@Autowired
	private AliyunVodService aliyunVodService;

	@GetMapping("/list/{chapterId}")
	@ApiOperation("获取章节视频")
	public R list(@PathVariable("chapterId") @Min(1) Integer chapterId) {
		List<EduVideoVO> list = eduVideoService.listVideos(chapterId);
		return RUtils.success("章节视频列表信息", list);
	}

	@GetMapping("/tmp/list/{chapterId}")
	@ApiOperation("获取章节视频")
	public R listTmp(@PathVariable("chapterId") @Min(1) Long chapterId) {
		List<EduVideoTmpVO> list = eduVideoTmpService.listVideos(chapterId);
		return RUtils.success("章节视频列表信息", list);
	}

	@GetMapping("/auth/{videoId}")
	@ApiOperation("获取视频播放凭证")
	public R getPlayAuth(@PathVariable("videoId") @NotEmpty String videoSourceId) {
		String auth = aliyunVodService.getPlayAuth(videoSourceId);
		if (StringUtils.hasText(auth)) {
			return RUtils.success("视频播放凭证", auth);
		}
		return RUtils.fail("获取播放链接失败");
	}

}
