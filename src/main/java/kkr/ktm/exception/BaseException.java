package kkr.ktm.exception;

public abstract class BaseException extends Exception {
	private boolean knowing = false;

	public BaseException() {
		super();
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public boolean isKnowing() {
		return knowing;
	}

	public void setKnowing(boolean knowing) {
		this.knowing = knowing;
	}
}
