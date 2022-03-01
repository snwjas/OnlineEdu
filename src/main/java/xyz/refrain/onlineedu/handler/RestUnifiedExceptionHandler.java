package xyz.refrain.onlineedu.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import xyz.refrain.onlineedu.constant.RS;
import xyz.refrain.onlineedu.exception.ServiceException;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理
 */
@RestControllerAdvice
@Slf4j
public class RestUnifiedExceptionHandler {

	/**
	 * 服务异常
	 */
	@ExceptionHandler({ServiceException.class})
	public R handleServiceException(ServiceException ex) {
		return RUtils.result(ex.getStatus(), ex.getMessage(), ex.getErrorData());
	}

	/**
	 * 参数校验失败异常
	 */
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public R handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.info(RS.ILLEGAL_PARAMETER.message(), ex.getCause());
		BindingResult bindingResult = ex.getBindingResult();
		Map<String, String> errors = new HashMap<>(8);
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return RUtils.fail(RS.ILLEGAL_PARAMETER, errors);
	}

	/**
	 * 参数校验失败异常
	 */
	@ExceptionHandler({ConstraintViolationException.class})
	public R handleConstraintViolationException(ConstraintViolationException ex) {
		log.info("{}：{}", RS.ILLEGAL_PARAMETER.message(), ex.getMessage());
		String[] messages = ex.getMessage().split(":");
		String msg = messages.length > 1 ? messages[1].trim() : "";
		return RUtils.fail(RS.ILLEGAL_PARAMETER, msg);
	}

	// @RequestParam 没有匹配到值
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public R handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		log.info("{}：{}", RS.ILLEGAL_PARAMETER.message(), ex.getMessage());
		return RUtils.fail(RS.ILLEGAL_PARAMETER, ex.getMessage());
	}

	// @RequestPart 没有匹配到值
	@ExceptionHandler(MissingServletRequestPartException.class)
	public R handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
		log.info("{}：{}", RS.ILLEGAL_PARAMETER.message(), ex.getMessage());
		return RUtils.fail(RS.ILLEGAL_PARAMETER, ex.getMessage());
	}

	// @RequestBody 没有匹配到值
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public R handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		log.info("{}：{}", RS.ILLEGAL_PARAMETER.message(), ex.getMessage());
		return RUtils.fail(RS.ILLEGAL_PARAMETER, ex.getMessage());
	}

	// BeanPropertyBindingResult Bean参数没有匹配
	@ExceptionHandler(BindException.class)
	public R handleBindException(BindException ex) {
		log.info(RS.ILLEGAL_PARAMETER.message(), ex.getCause());
		BindingResult bindingResult = ex.getBindingResult();
		String errorMessage = "";
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errorMessage = fieldError.getDefaultMessage();
			break;
		}
		return RUtils.fail(RS.ILLEGAL_PARAMETER, errorMessage);
	}

	// 上传文件过大
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public R handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
		log.info(RS.FILE_SIZE_LIMIT.message(), ex.getCause());
		return RUtils.fail(RS.FILE_SIZE_LIMIT);
	}

	// 非法参数
	@ExceptionHandler(NumberFormatException.class)
	public R handleNumberFormatException(NumberFormatException ex) {
		log.info("{}：{}", RS.ILLEGAL_PARAMETER.message(), ex.getMessage());
		return RUtils.fail(RS.ILLEGAL_PARAMETER, ex.getMessage());
	}

	// 最后处理
	@ExceptionHandler(Exception.class)
	public R handleException(Exception ex) {
		log.info("系统错误，路径：{}，错误：{}", IPUtils.getRequest().getServletPath(), ex.getMessage());
		ex.printStackTrace();
		return RUtils.fail(RS.SYSTEM_ERROR);
	}

}
