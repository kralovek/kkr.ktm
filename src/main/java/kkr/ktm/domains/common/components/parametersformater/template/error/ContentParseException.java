package kkr.ktm.domains.common.components.parametersformater.template.error;

import kkr.common.errors.BaseException;
import kkr.ktm.utils.parser.Position;

public class ContentParseException extends BaseException {
	private Position position;

	public ContentParseException(Position position, String message) {
		super(message);
		this.position = position;
	}

	public ContentParseException(Position position, String message, Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public String toString() {
		return "" //
				+ "\nCONTENT PARSER" //
				+ "\nPosition: " + position.toString() //
				+ "\nProblem: " + getMessage() //
				+ "\n";
	}
}
