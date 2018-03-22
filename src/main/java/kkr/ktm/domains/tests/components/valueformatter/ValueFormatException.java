package kkr.ktm.domains.tests.components.valueformatter;

import kkr.common.errors.BaseException;

public class ValueFormatException extends BaseException {

	public ValueFormatException(String message) {
		super(message);
	}

	public ValueFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
