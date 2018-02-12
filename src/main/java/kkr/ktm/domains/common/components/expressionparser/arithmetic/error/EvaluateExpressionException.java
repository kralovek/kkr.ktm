package kkr.ktm.domains.common.components.expressionparser.arithmetic.error;

import kkr.common.errors.BaseException;

public class EvaluateExpressionException extends BaseException {

	public EvaluateExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvaluateExpressionException(String message) {
		super(message);
	}
}
