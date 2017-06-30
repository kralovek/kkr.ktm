package kkr.ktm.domains.orchestrator.components.testlancer.debug;

import kkr.common.errors.ConfigurationException;

public class TestLancerDebugFwk {
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
