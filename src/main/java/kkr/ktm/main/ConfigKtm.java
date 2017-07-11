package kkr.ktm.main;

import java.util.Collection;

import kkr.common.errors.ConfigurationException;
import kkr.common.main.Config;

public class ConfigKtm extends Config {
	private String source;

	public ConfigKtm(Collection<String> args) throws ConfigurationException {
		super(args);

		if (config == null) {
			throw new ConfigurationException("Commandline parameter '-config' is not configured");
		}

		if (source == null) {
			throw new ConfigurationException("Commandline parameter '-source' is not configured");
		}
	}

	protected boolean configureParameter(String paramName, String paramValue) throws ConfigurationException {
		if (super.configureParameter(paramName, paramValue)) {
			return true;
		} else if ("-source".equals(paramName)) {
			source = paramValue;
			return true;
		} else {
			return false;
		}
	}

	public String getSource() {
		return source;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("\n") //
				.append("-source ").append(source).append("\n") //
				.append("-config ").append(config).append("\n");
		return buffer.toString();
	}
}
