package kkr.ktm.domains.common.components.formatter.auto;

import kkr.common.errors.ConfigurationException;

public abstract class FormatterFactoryAutoFwk extends FormatterAutoFwk {
	private boolean configured;

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
