package xyz.refrain.onlineedu.annotation;

import org.springframework.core.annotation.AliasFor;
import xyz.refrain.onlineedu.aspect.ActionRecordAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作记录
 *
 * @author Myles Yang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionRecord {

	/**
	 * 操作内容的EL表达式，仅支持方法参数
	 */
	@AliasFor("content")
	String value() default "";

	/**
	 * 操作内容，与 value 互为别名
	 */
	@AliasFor("value")
	String content() default "";

	/**
	 * 操作类型
	 */
	// LogType type() default LogType.COMMON;

	/**
	 * 生效条件的EL表达式<p>
	 * 它是通过返回值判断，返回值符号为{@link ActionRecordAspect#METHOD_RETURNING_SIGN}<p>
	 * 示例（函数返回值为0）：<p>
	 * EL表达式："#ret > 0"，结果 false
	 */
	String condition() default "";

}
