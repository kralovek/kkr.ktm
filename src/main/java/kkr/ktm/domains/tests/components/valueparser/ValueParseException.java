package kkr.ktm.domains.tests.components.valueparser;

import kkr.common.errors.BaseException;
import kkr.ktm.utils.parser.Position;

public class ValueParseException extends BaseException {
	private Position position;

	public ValueParseException(Position position, String message) {
		super(message);
		this.position = position;
	}

	public ValueParseException(Position position, String message, Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public String toString() {
		return "" //
				+ "\nCONTENT PARSER" //
				+ (position != null ? "\nPosition: " + position.toString() : "") //
				+ "\nProblem: " + getMessage() //
				+ "\n";
	}
}
