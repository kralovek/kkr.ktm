package kkr.ktm.domains.common.components.expressionparser.arithmetic.error;

import kkr.common.errors.BaseException;

public class ExpressionEvaluateException extends BaseException {

	public ExpressionEvaluateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionEvaluateException(String message) {
		super(message);
	}
}
