package kkr.ktm.domains.tests.components.testreporter.empty;

import kkr.common.errors.ConfigurationException;

public abstract class TestReporterEmptyFwk {
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
