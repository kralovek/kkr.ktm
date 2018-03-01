package kkr.ktm.domains.common.components.parametersformater.template2.error;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.parametersformater.template2.Position;

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
		return position.toString() + " " + getMessage();
	}
}
