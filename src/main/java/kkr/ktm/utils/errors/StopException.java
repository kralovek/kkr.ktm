package kkr.ktm.utils.errors;

import kkr.ktm.exception.BaseException;

public class StopException extends BaseException {

	public StopException() {
		super();
	}

	public StopException(String message) {
		super(message);
	}
}
