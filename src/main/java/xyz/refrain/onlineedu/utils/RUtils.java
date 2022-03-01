package xyz.refrain.onlineedu.utils;

import org.springframework.http.HttpStatus;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.model.vo.R;

/**
 * 统一 响应 工具
 *
 * @author Myles Yang
 */
public class RUtils {

	private static final Object EMPTY_DATA = null;

	/**
	 * 返回 统一 响应体
	 *
	 * @param status  状态码
	 * @param message 消息
	 * @param obj     数据
	 * @return R
	 */
	public static R result(int status, String message, Object obj) {
		return new R(status, message, obj);
	}

	/**
	 * 普通的成功失败响应
	 *
	 * @param i       小于1失败，否则成功
	 * @param message 消息
	 */
	public static R commonFailOrNot(int i, String message) {
		if (i < 1) {
			return fail(message + "失败");
		}
		return success(message + "成功");
	}


	public static R success(String message, Object data) {
		return result(RS.SUCCESS.status(), message, data);
	}

	public static R success(String message) {
		return success(message, EMPTY_DATA);
	}

	public static R succeed() {
		return success(RS.SUCCESS.message());
	}

	///////////////////////////////////////////////////////////////////////////////

	/**
	 * 失败，不传 status ，默认系统错误
	 */
	public static R fail(String message, Object data) {
		return result(RS.SYSTEM_ERROR.status(), message, data);
	}

	public static R fail(String message) {
		return fail(message, EMPTY_DATA);
	}


	public static R fail(RS status, Object data) {
		return result(status.status(), status.message(), data);
	}

	public static R fail(RS status) {
		return result(status.status(), status.message(), EMPTY_DATA);
	}


	public static R fail(HttpStatus httpStatus, Object data) {
		return result(httpStatus.value(), httpStatus.getReasonPhrase(), data);
	}

	public static R fail(HttpStatus httpStatus) {
		return fail(httpStatus, EMPTY_DATA);
	}


}
