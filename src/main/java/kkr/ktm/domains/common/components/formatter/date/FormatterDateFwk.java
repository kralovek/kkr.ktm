package kkr.ktm.domains.common.components.formatter.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import kkr.common.errors.ConfigurationException;

public abstract class FormatterDateFwk {
	private boolean configured;

	protected DateFormat dateFormat;
	private String _pattern;

	protected void configPattern(String pattern) throws ConfigurationException {
		if (pattern == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'pattern' is not configured");
		} else {
			dateFormat = new SimpleDateFormat(pattern);
			try {
				dateFormat.format(new Date());
			} catch (Exception ex) {
				throw new ConfigurationException(
						getClass().getSimpleName() + ": Parameter 'pattern' has bad value: " + pattern, ex);
			}
		}
	}

	public void config() throws ConfigurationException {
		configured = false;
		configPattern(_pattern);
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getPattern() {
		return _pattern;
	}

	public void setPattern(String pattern) {
		this._pattern = pattern;
	}
}
