package kkr.ktm.components.testsinitializer.empty;

import kkr.ktm.exception.ConfigurationException;

public abstract class TestInitializerEmptyFwk {
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
