package xyz.refrain.onlineedu.exception;

/**
 * Base exception of the project.
 */
public abstract class MyException extends RuntimeException {

	private static final long serialVersionUID = 4140200838147465959L;

	private Object errorData;

	public MyException(String message) {
		super(message);
	}

	public MyException(String message, Throwable cause) {
		super(message, cause);
	}

	public abstract int getStatus();

	public Object getErrorData() {
		return errorData;
	}

	public void setErrorData(Object errorData) {
		this.errorData = errorData;
	}
}
