package kkr.ktm.domains.common.components.expressionparser.arithmetic.error;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;

public class ParseExpressionException extends BaseException {

	private Position position;

	public ParseExpressionException(Position position, String message) {
		super(message);
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}
}
