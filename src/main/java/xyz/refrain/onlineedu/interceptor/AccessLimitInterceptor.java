package xyz.refrain.onlineedu.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.refrain.onlineedu.annotation.AccessLimit;
import xyz.refrain.onlineedu.constant.CacheKeyPrefix;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RWriterUtils;
import xyz.refrain.onlineedu.utils.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 接口限流防刷拦截器
 *
 * @author Myles Yang
 */
public class AccessLimitInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();

		AccessLimit annotation = method.getAnnotation(AccessLimit.class);
		if (Objects.nonNull(annotation)) {
			return isAccess(annotation, request, response);
		}

		return true;
	}

	/**
	 * 是否通行
	 */
	private boolean isAccess(AccessLimit annotation, HttpServletRequest request, HttpServletResponse response) {

		int maxCount = annotation.maxCount();
		int seconds = annotation.seconds();

		String key = CacheKeyPrefix.ACCESS_LIMIT_PREFIX
				+ IPUtils.getIpAddress(request)
				+ request.getRequestURI();

		Integer count = (Integer) RedisUtils.get(key);
		if (Objects.nonNull(count)) {
			if (count < maxCount) {
				RedisUtils.set(key, count + 1, seconds);
			} else {
				RWriterUtils.writeJson(response, RUtils.fail(RS.FREQUENT_OPERATION));
				return false;
			}
		} else {
			RedisUtils.set(key, 1, seconds);
		}

		return true;
	}
}
