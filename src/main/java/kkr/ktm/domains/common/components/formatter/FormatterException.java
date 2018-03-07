package kkr.ktm.domains.common.components.formatter;

import kkr.common.errors.BaseException;

public class FormatterException extends BaseException {

	public FormatterException(String message, Object object, Throwable cause) {
		super(message, cause);
	}

	public FormatterException(String message, Object object) {
		super(message);
	}

}
