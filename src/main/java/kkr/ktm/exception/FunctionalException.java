package kkr.ktm.exception;

public class FunctionalException extends BaseException {

	public FunctionalException() {
		super();
	}

	public FunctionalException(String message, Throwable cause) {
		super(message, cause);
	}

	public FunctionalException(String message) {
		super(message);
	}

	public FunctionalException(Throwable cause) {
		super(cause);
	}
}
