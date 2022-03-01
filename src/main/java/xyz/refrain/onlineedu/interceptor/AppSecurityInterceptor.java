package xyz.refrain.onlineedu.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.utils.RWriterUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 前台登录安全拦截器
 *
 * @author Myles Yang
 */
public class AppSecurityInterceptor extends AbstractSecurityInterceptor implements HandlerInterceptor {

	public AppSecurityInterceptor() {
		addPathPatterns("/api/app/**");
		excludePathPatterns("/api/app/member/login", "/api/app/member/register", "/api/app/pub/**");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!SessionUtils.checkMemberLogin(request)) {
			RWriterUtils.writeJson(response, new R(RS.NOT_LOGIN.status(), "请登录后再操作"));
			return false;
		}

		return true;
	}

}
