package kkr.ktm.domains.common.components.calculator.context;

import kkr.common.errors.ConfigurationException;

public abstract class CalculatorContextFwk {
	private boolean configured;

	public void config() throws ConfigurationException {
		configured = false;
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

}
