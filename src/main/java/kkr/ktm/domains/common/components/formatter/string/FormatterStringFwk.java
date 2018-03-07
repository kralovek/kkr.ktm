package kkr.ktm.domains.common.components.formatter.string;

import java.util.Locale;

import kkr.common.errors.ConfigurationException;

public abstract class FormatterStringFwk {
	private boolean configured;

	protected static final Locale LOCALE = Locale.US;

	protected String pattern;

	protected void configPattern(String pattern) throws ConfigurationException {
		if (pattern == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'pattern' is not configured");
		} else {
			try {
				java.util.Formatter formatter = new java.util.Formatter(LOCALE);
				formatter.format(pattern, "x");
				formatter.close();
			} catch (Exception ex) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Parameter 'pattern' has bad value. It's not a valid decimal format string", ex);
			}
		}

	}

	public void config() throws ConfigurationException {
		configured = false;
		configPattern(pattern);
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
