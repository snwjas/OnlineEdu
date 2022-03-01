package xyz.refrain.onlineedu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口防刷限流（固定时间内最大访问次数）
 *
 * @author Myles Yang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

	/**
	 * 最大访问次数
	 */
	int maxCount() default Integer.MAX_VALUE;

	/**
	 * 固定时间
	 */
	int seconds() default 1;

}
