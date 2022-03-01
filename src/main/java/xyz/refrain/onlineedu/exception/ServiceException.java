package xyz.refrain.onlineedu.exception;

import org.springframework.http.HttpStatus;
import xyz.refrain.onlineedu.constant.RS;

/**
 * 服务异常
 */
public class ServiceException extends MyException {

	private static final long serialVersionUID = 7951201720502956459L;

	private final int status;

	public ServiceException() {
		super(RS.SYSTEM_ERROR.message());
		this.status = RS.SYSTEM_ERROR.status();
	}

	public ServiceException(String message) {
		super(message);
		this.status = RS.SYSTEM_ERROR.status();
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		this.status = RS.SYSTEM_ERROR.status();
	}

	public ServiceException(RS status) {
		super(status.message());
		this.status = status.status();
	}

	public ServiceException(HttpStatus status) {
		super(status.getReasonPhrase());
		this.status = status.value();
	}

	@Override
	public int getStatus() {
		return status;
	}


}
