package kkr.ktm.utils.errors;

import kkr.common.errors.BaseException;

public class StopException extends BaseException {

	public StopException() {
		super();
	}

	public StopException(String message) {
		super(message);
	}
}
