package xyz.refrain.onlineedu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RWriterUtils;

import java.util.List;

/**
 * WebMvc配置
 *
 * @author Myles Yang
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {


	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * 监听HTTP请求事件
	 * 解决 RequestContextHolder.getRequestAttributes() 空指针问题
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	/**
	 * 跨域访问配置
	 */
	@Bean
	@Profile({"dev", "test"})
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {

		// swagger-ui doc enable
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
		super.addResourceHandlers(registry);
	}

	/**
	 * 配置拦截器
	 */
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		// 前端 api 拦截器
		xyz.refrain.onlineedu.interceptor.AppSecurityInterceptor appSecurityInterceptor = new xyz.refrain.onlineedu.interceptor.AppSecurityInterceptor();
		registry.addInterceptor(appSecurityInterceptor)
				.addPathPatterns(appSecurityInterceptor.getPathPatterns())
				.excludePathPatterns(appSecurityInterceptor.getExcludePatterns());

		// 讲师端 api 拦截器
		xyz.refrain.onlineedu.interceptor.TeacherSecurityInterceptor teacherSecurityInterceptor = new xyz.refrain.onlineedu.interceptor.TeacherSecurityInterceptor();
		registry.addInterceptor(teacherSecurityInterceptor)
				.addPathPatterns(teacherSecurityInterceptor.getPathPatterns())
				.excludePathPatterns(teacherSecurityInterceptor.getExcludePatterns());

		// 管理员端 api 拦截器
		xyz.refrain.onlineedu.interceptor.AdminSecurityInterceptor adminSecurityInterceptor = new xyz.refrain.onlineedu.interceptor.AdminSecurityInterceptor();
		registry.addInterceptor(adminSecurityInterceptor)
				.addPathPatterns(adminSecurityInterceptor.getPathPatterns())
				.excludePathPatterns(adminSecurityInterceptor.getExcludePatterns());

		// 接口限流拦截器
		registry.addInterceptor(new xyz.refrain.onlineedu.interceptor.AccessLimitInterceptor());

		// 数据统计拦截器
		xyz.refrain.onlineedu.interceptor.StatInterceptor statInterceptor = new xyz.refrain.onlineedu.interceptor.StatInterceptor();
		registry.addInterceptor(statInterceptor)
				.addPathPatterns(statInterceptor.getPathPatterns())
				.excludePathPatterns(statInterceptor.getExcludePatterns());

		super.addInterceptors(registry);
	}


	/**
	 * 自定义json消息转换器
	 */
	@Bean
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	/**
	 * 配置消息转换器
	 */
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(customJackson2HttpMessageConverter());
		super.configureMessageConverters(converters);
	}

	/**
	 * error page 返回json（自定义错误页面）
	 */
	@Bean("error")
	public View error() {
		// return new MappingJackson2JsonView();
		return (model, request, response) -> {
			RWriterUtils.writeJson(response,
					RUtils.fail(RS.PAGE_NOT_FOUND, model.get("path")));
		};
	}

}
