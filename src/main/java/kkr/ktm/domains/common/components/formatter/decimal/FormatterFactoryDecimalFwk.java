package kkr.ktm.domains.common.components.formatter.decimal;

import kkr.common.errors.ConfigurationException;

public abstract class FormatterFactoryDecimalFwk extends FormatterDecimalFwk {
	private boolean configured;

	protected void configPattern(String pattern) throws ConfigurationException {
		// NOTHING
	}

	public void config() throws ConfigurationException {
		configured = false;
		super.config();
		configured = true;
	}

	public void testConfigured() {
		super.testConfigured();
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}
}
