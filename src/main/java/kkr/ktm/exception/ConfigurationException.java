package kkr.ktm.exception;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationException extends BaseException {

	private List<String> messages = new ArrayList<String>();

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public List<String> getMessages() {
		return messages;
	}
}
