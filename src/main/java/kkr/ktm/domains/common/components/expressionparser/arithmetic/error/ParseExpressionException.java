package kkr.ktm.domains.common.components.expressionparser.arithmetic.error;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;

public class ParseExpressionException extends BaseException {

	private Position position;
	private String expression;
	private String part;

	public ParseExpressionException(Position position, String message, String part) {
		super(message);
		this.position = position;
		this.part = part;
	}

	public ParseExpressionException(Position position, String message, String part, Throwable cause) {
		super(message, cause);
		this.position = position;
		this.part = part;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Position getPosition() {
		return position;
	}

	public String toString() {
		return "" //
				+ (expression != null ? "Expression: '" + expression + "' " : "") //
				+ "Position: " + position.toString() + " "//
				+ "Part: '" + part + "' "//
				+ "Error: " + getMessage();
	}
}
