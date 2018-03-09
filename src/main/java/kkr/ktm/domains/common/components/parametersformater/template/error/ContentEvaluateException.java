package kkr.ktm.domains.common.components.parametersformater.template.error;

import kkr.common.errors.BaseException;
import kkr.ktm.utils.parser.Position;

public class ContentEvaluateException extends BaseException {

	private Position position;

	public ContentEvaluateException(Position position, String message) {
		super(message);
		this.position = position;
	}

	public ContentEvaluateException(Position position, String message, Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public String toString() {
		return position.toString() + " " + getMessage();
	}
}
