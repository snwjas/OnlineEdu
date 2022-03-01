package xyz.refrain.onlineedu.constant;

import xyz.refrain.onlineedu.controller.app.UctrMemberController;
import xyz.refrain.onlineedu.model.params.RegisterParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统计缓存key前缀
 *
 * @author Myles Yang
 */
public interface StatConstant {

	/**
	 * 分隔符
	 */
	String SEPARATOR = ":";

	/**
	 * 一天秒数
	 */
	long ONE_DAY_SECONDS = 86400L;

	/**
	 * 网站访问量（key+ip）
	 * {@link xyz.refrain.onlineedu.interceptor.StatInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
	 */
	String VISIT_COUNT = "visitCount:";

	/**
	 * 注册量(inc)
	 * {@link xyz.refrain.onlineedu.controller.app.UctrMemberController#register(RegisterParam)}
	 */
	String REGISTER_COUNT = "registerCount:";

	/**
	 * 登录量/活跃人数（key+memberId）
	 * {@link UctrMemberController#info()}
	 */
	String LOGIN_COUNT = "loginCount:";

	/**
	 * 视频播放量(key+memberId+videoId)
	 * {@link xyz.refrain.onlineedu.controller.app.ContentController#getVideoPlayAuth(Integer, Integer, String)}
	 */
	String VIDEO_VIEW_COUNT = "videoViewCount:";

	/**
	 * 课程订阅量(inc)
	 * {@link xyz.refrain.onlineedu.controller.app.ContentController#orderPaySucceed(String, HttpServletRequest)}
	 */
	String COURSE_BUY_COUNT = "courseBuyCount:";

}
