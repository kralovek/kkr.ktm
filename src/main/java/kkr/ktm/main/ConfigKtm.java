package kkr.ktm.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kkr.common.errors.ConfigurationException;

public class ConfigKtm {
	private String source;
	private String config = "spring-main-ktm.xml";
	private List<String> properties = new ArrayList<String>();
	private Map<String, String> parameters = new LinkedHashMap<String, String>();

	private List<String> messages = new ArrayList<String>();

	public ConfigKtm(String[] args) throws ConfigurationException {
		init(args);
	}

	public String getSource() {
		return source;
	}

	public String getConfig() {
		return config;
	}

	public List<String> getProperties() {
		return properties;
	}

	public List<String> getMessages() {
		return messages;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	protected void init(final String[] pArgs) throws ConfigurationException {
		if (pArgs.length % 2 != 0) {
			throw new ConfigurationException("A command-line parameter is missing a value");
		}
		for (int i = 0; i < pArgs.length; i += 2) {
			if (pArgs[i + 1] == null || pArgs[i + 1].isEmpty()) {
				throw new ConfigurationException("Value of the parameter '" + pArgs[i] + "' is empty");
			}
			if ("-config".equals(pArgs[i])) {
				config = pArgs[i + 1];
			} else if ("-properties".equals(pArgs[i])) {
				properties = pipesToList(pArgs[i + 1], true);
				if (properties == null) {
					throw new ConfigurationException("Parameter -properties must contain a list of ressources separated by |");
				}
			} else if ("-messages".equals(pArgs[i])) {
				messages = pipesToList(pArgs[i + 1], false);
				if (messages == null) {
					throw new ConfigurationException("Parameter -messages must contain a list of messages separated by |");
				}
			} else if ("-source".equals(pArgs[i])) {
				source = pArgs[i + 1];
			} else if ("-parameters".equals(pArgs[i])) {
				List<String> paramatersValues = pipesToList(pArgs[i + 1], true);
				if (paramatersValues == null) {
					throw new ConfigurationException("Parameter -parameters must contain a list of parameter=value separated by |");
				}
				for (String parameterValue : paramatersValues) {
					String[] pv = separateParameterValue(parameterValue);
					parameters.put(pv[0], pv[1]);
				}
			}
		}
	}

	private String[] separateParameterValue(String parameterValue) {
		String[] pv = parameterValue.split("=");
		if (pv.length != 2) {
			return null;
		}
		pv[0] = pv[0].trim();
		if (pv[0].trim().isEmpty()) {
			return null;
		}
		pv[1] = pv[1].trim();
		return pv;
	}

	private List<String> pipesToList(String value, boolean excludeEmpty) throws ConfigurationException {
		List<String> values = new ArrayList<String>();
		String[] arrValues = value.split("\\|");
		for (int j = 0; j < arrValues.length; j++) {
			arrValues[j] = arrValues[j].trim();
			if (arrValues[j].isEmpty()) {
				if (!excludeEmpty) {
					continue;
				}
			}
			values.add(arrValues[j]);
		}
		return values;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("\n") //
				.append("-source ").append(source).append("\n") //
				.append("-config ").append(config).append("\n") //
				.append("-properties ").append(toString(properties)).append("\n") //
				.append("-parameters ").append(toString(parameters)).append("\n");
		return buffer.toString();
	}

	private String toString(Map<String, String> parameters) {
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (buffer.length() != 0) {
				buffer.append("|");
			}
			buffer.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return buffer.toString();
	}

	private String toString(List<String> strings) {
		StringBuffer buffer = new StringBuffer();
		for (String str : strings) {
			if (buffer.length() != 0) {
				buffer.append("|");
			}
			buffer.append(str);
		}
		return buffer.toString();
	}

	private String toString(String[] strings) {
		StringBuffer buffer = new StringBuffer();
		for (String str : strings) {
			if (buffer.length() != 0) {
				buffer.append("|");
			}
			buffer.append(str);
		}
		return buffer.toString();
	}
}
