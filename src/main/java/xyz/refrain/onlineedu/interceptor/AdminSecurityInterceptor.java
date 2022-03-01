package xyz.refrain.onlineedu.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RWriterUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员端登录安全拦截器
 *
 * @author Myles Yang
 */
public class AdminSecurityInterceptor extends AbstractSecurityInterceptor implements HandlerInterceptor {

	public AdminSecurityInterceptor() {
		addPathPatterns("/api/admin/**");
		excludePathPatterns("/api/admin/user/login");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!SessionUtils.checkAclUserLogin(request)) {
			RWriterUtils.writeJson(response, RUtils.fail(RS.NOT_LOGIN));
			return false;
		}

		return true;
	}

}
