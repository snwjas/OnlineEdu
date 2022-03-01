package xyz.refrain.onlineedu.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import xyz.refrain.onlineedu.constant.StatConstant;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据统计拦截器
 *
 * @author Myles Yang
 */
public class StatInterceptor extends AbstractSecurityInterceptor implements HandlerInterceptor {

	public StatInterceptor() {
		addPathPatterns("/api/app/**");
		excludePathPatterns();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// 统计网站访问人数

		String ipAddress = IPUtils.getIpAddress(request);

		String key = StatConstant.VISIT_COUNT + ipAddress;

		// 统计时统计key的数量，缓存在统计完成时进行删除
		RedisUtils.set(key, null);

		return true;
	}

}
