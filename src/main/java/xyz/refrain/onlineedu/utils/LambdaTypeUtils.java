package xyz.refrain.onlineedu.utils;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 通过 Lambda 获取对象属性名
 * 使用方法： LambdaTypeUtils.getFieldName(Test::getName)
 * 或
 * 使用MyBatis-plus 获取列名
 * 需要正确设置实体字段的的@TableField注解
 */
@Slf4j
public class LambdaTypeUtils {

	@FunctionalInterface
	public interface SFunction<T> extends Serializable {
		Object get(T source);
	}

	private static final String SERIALIZED_LAMBDA_KEY = "SLK:";

	private static final String COLUMN_NAME_KEY = "CNK:";

	private static final int KEY_TIMEOUT_SECONDS = 86400; // one day

	/**
	 * 获取字段名
	 * 确保 字段上有注解 @TableField
	 */
	public static <T> String getColumnName(SFunction<T> fn) {
		SerializedLambda lambda = getSerializedLambda(fn);
		if (lambda == null) return "";

		String fullName = lambda.getImplClass().replaceAll("/", ".");
		String fieldName = getFieldName(fn);

		String columnName = (String) RedisUtils.get(COLUMN_NAME_KEY + fullName + "." + fieldName);
		if (null == columnName) {
			try {
				Class<?> aClass = Class.forName(fullName);
				Field field = aClass.getDeclaredField(fieldName);
				TableField annotation = field.getAnnotation(TableField.class);
				columnName = annotation.value();
				RedisUtils.set(COLUMN_NAME_KEY + fullName + "." + fieldName, columnName, KEY_TIMEOUT_SECONDS);
			} catch (NoSuchFieldException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return columnName;
	}

	/***
	 * 转换方法引用为属性名
	 */
	public static <T> String getFieldName(SFunction<T> fn) {
		SerializedLambda lambda = getSerializedLambda(fn);
		if (lambda == null) return "";
		// 获取方法名
		String methodName = lambda.getImplMethodName();
		String prefix = null;
		if (methodName.startsWith("get")) {
			prefix = "get";
		} else if (methodName.startsWith("is")) {
			prefix = "is";
		}
		if (prefix == null) {
			log.error("无效的getter方法：{}", methodName);
			return "";
		}
		// 截取get/is之后的字符串并转换首字母为小写
		return toLowerCaseFirstOne(methodName.replace(prefix, ""));
	}

	/**
	 * 首字母转小写
	 */
	private static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return Character.toLowerCase(s.charAt(0)) + s.substring(1);
		}
	}

	/**
	 * 关键在于这个方法
	 */
	private static SerializedLambda getSerializedLambda(Serializable fn) {
		SerializedLambda lambda = (SerializedLambda) RedisUtils.get(SERIALIZED_LAMBDA_KEY + fn.hashCode());
		if (null == lambda) {
			try {
				// 提取SerializedLambda并缓存
				Method method = fn.getClass().getDeclaredMethod("writeReplace");
				method.setAccessible(Boolean.TRUE);
				lambda = (SerializedLambda) method.invoke(fn);
				RedisUtils.set(SERIALIZED_LAMBDA_KEY + fn, lambda, KEY_TIMEOUT_SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lambda;
	}


}

