package kkr.ktm.components.batchreporter.screen;

import kkr.ktm.exception.BaseException;

public abstract class BatchReporterScreenFwk {
	private boolean configured;

	public void config() throws BaseException {
		configured = false;
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}
}
