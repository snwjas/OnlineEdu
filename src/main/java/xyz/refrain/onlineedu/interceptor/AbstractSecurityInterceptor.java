package xyz.refrain.onlineedu.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 抽象的安全拦截器
 *
 * @author Myles Yang
 */
public abstract class AbstractSecurityInterceptor {
	/**
	 * 拦截的 uri
	 */
	protected List<String> pathPatterns;

	/**
	 * 放行的 uri
	 */
	protected List<String> excludePatterns;

	public AbstractSecurityInterceptor() {
		this.pathPatterns = new ArrayList<>();
		this.excludePatterns = new ArrayList<>();
	}

	protected AbstractSecurityInterceptor addPathPatterns(String... pathPatterns) {
		this.pathPatterns.addAll(Arrays.asList(pathPatterns));
		return this;
	}

	protected AbstractSecurityInterceptor excludePathPatterns(String... excludePatterns) {
		this.excludePatterns.addAll(Arrays.asList(excludePatterns));
		return this;
	}

	public List<String> getPathPatterns() {
		return this.pathPatterns;
	}

	public List<String> getExcludePatterns() {
		return this.excludePatterns;
	}

}
