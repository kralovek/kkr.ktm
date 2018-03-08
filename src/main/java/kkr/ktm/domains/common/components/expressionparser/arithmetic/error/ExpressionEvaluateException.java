package kkr.ktm.domains.common.components.expressionparser.arithmetic.error;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;

public class ExpressionEvaluateException extends BaseException {
	private Position position;
	private String expression;

	public ExpressionEvaluateException(Position position, String expression, String message, Throwable cause) {
		super(message, cause);
		this.position = position;
		this.expression = expression;
	}

	public ExpressionEvaluateException(Position position, String expression, String message) {
		super(message);
		this.position = position;
		this.expression = expression;
	}

	public Position getPosition() {
		return position;
	}

	public String getExpression() {
		return expression;
	}
}
