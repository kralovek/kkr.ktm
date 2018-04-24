package kkr.ktm.domains.common.components.calculator.text;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.Formatter;

public abstract class CalculatorTextFwk {
	private boolean configured;

	protected Formatter formatter;

	public void config() throws ConfigurationException {
		configured = false;
		if (formatter == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'formatter' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}
}
