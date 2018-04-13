package kkr.ktm.domains.common.components.expressionparser.generic.error;

import kkr.common.errors.BaseException;
import kkr.ktm.utils.parser.Position;

public class ExpressionParseException extends BaseException {

	private Position position;
	private String expression;
	private String part;

	public ExpressionParseException(Position position, String message, String part) {
		super(message);
		this.position = position;
		this.part = part;
	}

	public ExpressionParseException(Position position, String message, String part, Throwable cause) {
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
		return "EXPRESSION PARSER" //
				+ "\n" + (expression != null ? "Expression: '" + expression + "' " : "") //
				+ "\nPosition: " + position.toString() + " "//
				+ "\nPart: '" + part + "' "//
				+ "\nProblem: " + getMessage() //
				+ "\n";
	}
}
