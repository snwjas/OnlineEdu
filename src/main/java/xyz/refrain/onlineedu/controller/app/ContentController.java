package xyz.refrain.onlineedu.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.constant.StatConstant;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;
import xyz.refrain.onlineedu.model.params.EduCommentSearchParam;
import xyz.refrain.onlineedu.model.params.EduCourseSearchParam;
import xyz.refrain.onlineedu.model.params.TOrderSearchParam;
import xyz.refrain.onlineedu.model.securtiy.EduTeacherDetail;
import xyz.refrain.onlineedu.model.securtiy.UctrMemberDetail;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduSubjectSimpleParentVO;
import xyz.refrain.onlineedu.model.vo.admin.TOrderVO;
import xyz.refrain.onlineedu.model.vo.teacher.EduChapterVO;
import xyz.refrain.onlineedu.model.vo.teacher.EduCommentVO;
import xyz.refrain.onlineedu.model.vo.teacher.EduVideoVO;
import xyz.refrain.onlineedu.service.*;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RedisUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 前台内容控制器（无需登录即可获取数据）
 *
 * @author Myles Yang
 */
@Validated
@RestController("AppContentController")
@RequestMapping("/api/app/pub/content")
@Api(value = "前台内容控制器", tags = {"前台内容接口"})
public class ContentController {

	@Autowired
	private EduSubjectService eduSubjectService;

	@Autowired
	private HmBannerService hmBannerService;

	@Autowired
	private EduCourseService eduCourseService;

	@Autowired
	private EduChapterService eduChapterService;

	@Autowired
	private EduVideoService eduVideoService;

	@Autowired
	private EduCommentService eduCommentService;

	@Autowired
	private EduTeacherService eduTeacherService;

	@Autowired
	private TOrderService tOrderService;


	@GetMapping("/get/subjects")
	@ApiOperation("获取所有分类")
	public R getSubjects() {
		return eduSubjectService.get();
	}

	@GetMapping("/get/subject/parent/{id}")
	@ApiOperation("获取分类父分类")
	public R getSubjectParent(@PathVariable("id") @Min(1) Integer id) {
		EduSubjectSimpleParentVO parent = eduSubjectService.listAllParent(id);
		return RUtils.success("父分类", parent);
	}

	@GetMapping("/get/banners")
	@ApiOperation("获取所有banner")
	public R getBanners() {
		return hmBannerService.get();
	}

	@PostMapping("/get/courses")
	@ApiOperation("获取所有课程")
	public R getCourses(@RequestBody @Valid EduCourseSearchParam param) {
		param.setPageSize(12);
		return eduCourseService.listForApp(param);
	}

	@GetMapping("/get/courses/{id}")
	@ApiOperation("获取课程详情")
	public R getCourseDetail(@PathVariable("id") @Min(1) Integer id) {
		return eduCourseService.getDetails(id);
	}

	@GetMapping("/get/teacher/{id}")
	@ApiOperation("获取讲师详情信息")
	public R getTeacher(@PathVariable("id") @Min(1) Integer id) {
		return eduTeacherService.getTeacherForApp(id);
	}

	@GetMapping("/get/course/isbuy/{id}")
	@ApiOperation("判断学员是否已经订阅课程")
	public R getIsBuyCourse(@PathVariable("id") @Min(1) Integer id, HttpServletRequest request) {
		UctrMemberDetail member = SessionUtils.getMember(request);
		if (Objects.isNull(member)) {
			return new R(RS.NOT_LOGIN.status(), "请登录后再操作");
		}
		return eduCourseService.isBuyCourse(member.getId(), id);
	}

	@PostMapping("/get/course/orders")
	@ApiOperation("获取课程订阅订单列表")
	public R getCourseOrders(@RequestBody @Valid TOrderSearchParam param, HttpServletRequest request) {
		UctrMemberDetail member = SessionUtils.getMember(request);
		if (Objects.isNull(member)) {
			return new R(RS.NOT_LOGIN.status(), "请登录后再操作");
		}
		param.setMemberId(member.getId());
		return tOrderService.listMemberOrders(param);
	}

	@GetMapping("/get/chapters/{courseId}")
	@ApiOperation("获取课程章节")
	public R getChapters(@PathVariable("courseId") @Min(1) Integer courseId) {
		List<EduChapterVO> list = eduChapterService.listChapters(courseId);
		return RUtils.success("章节列表信息", list);
	}

	@GetMapping("/get/videos/{chapterId}")
	@ApiOperation("获取章节视频")
	public R getChapterVideos(@PathVariable("chapterId") @Min(1) Integer chapterId) {
		List<EduVideoVO> list = eduVideoService.listVideos(chapterId);
		return RUtils.success("章节视频列表信息", list);
	}

	@PostMapping("/get/video/auth")
	@ApiOperation("获取视频播放凭证")
	public R getVideoPlayAuth(@RequestParam("courseId") @NotNull @Min(1) Integer courseId,
	                          @RequestParam("videoId") @NotNull @Min(1) Integer videoId,
	                          @RequestParam("videoSourceId") @NotEmpty String videoSourceId) {
		R r = eduVideoService.getVideoPlayAuth(courseId, videoId, videoSourceId);

		// 统计视频播放量（播放视频必须是已登录的）
		UctrMemberDetail member = SessionUtils.getMember(IPUtils.getRequest());
		String key = StatConstant.VIDEO_VIEW_COUNT + member.getId() + StatConstant.SEPARATOR + videoId;
		RedisUtils.set(key, null);

		return r;
	}

	@PostMapping("/get/course/comment")
	@ApiOperation("列出课程评论")
	public R getCourseComment(@RequestBody @Valid EduCommentSearchParam param) {
		param.setStatus(true);
		param.setPageSize(10);
		return eduCommentService.list(param);
	}

	@PostMapping("/publish/comment")
	@ApiOperation("发表课程评论")
	public R publishComment(@RequestBody @Validated(ValidGroupType.Save.class) EduCommentVO vo) {
		return eduCommentService.publish(vo);
	}

	@PostMapping("/teacher/apply")
	@ApiOperation("讲师申请")
	public R teacherApply(@Validated EduTeacherDetail detail,
	                      @RequestPart(value = "file", required = false) MultipartFile file,
	                      @RequestPart(value = "resumeFile", required = false) MultipartFile resume) throws IOException {
		if (Objects.isNull(file)) {
			return RUtils.fail("头像不能为空");
		}
		if (Objects.isNull(resume)) {
			return RUtils.fail("简历不能为空");
		}
		detail.setPassword(null);
		R r = eduTeacherService.create(detail, file, resume, TeacherStatusEnum.AUDITING);
		if (r.getStatus() == RS.SUCCESS.status()) {
			return RUtils.success("申请成为讲师成功，待管理员审核");
		} else {
			return r;
		}
	}

	@PostMapping("/course/pay/create")
	@ApiOperation("创建课程支付订单")
	public R createOrder(@RequestBody @Validated TOrderVO vo, HttpServletRequest request) {
		UctrMemberDetail member = SessionUtils.getMember(request);
		if (Objects.isNull(member)) {
			return new R(RS.NOT_LOGIN.status(), "请登录后再操作");
		}
		vo.setMemberId(member.getId());
		return tOrderService.createOrder(vo);
	}

	@PostMapping("/course/pay/succeed/{orderNo}")
	@ApiOperation("课程支付成功")
	public R orderPaySucceed(@PathVariable("orderNo") @NotEmpty String orderNo, HttpServletRequest request) {
		if (!SessionUtils.checkMemberLogin(request)) {
			return new R(RS.NOT_LOGIN.status(), "请登录后再操作");
		}
		R r = tOrderService.paySucceed(orderNo);

		// 统计课程订阅量
		if (RS.SUCCESS.status() == r.getStatus()) {
			RedisUtils.incr(StatConstant.COURSE_BUY_COUNT, 1);
		}

		return r;
	}

}
