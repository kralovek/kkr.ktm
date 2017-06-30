package kkr.ktm.domains.common.components.filemanager.local;

import kkr.common.errors.ConfigurationException;



public abstract class FileManagerLocalFwk {
	private boolean configured;

	protected String traceDataFile;

	public void config() throws ConfigurationException {
		configured = false;
		if (traceDataFile == null) {
			// OK
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("[").append(this.getClass().getName()).append("]\n");
		return buffer.toString();
	}

	public String getTraceDataFile() {
		return traceDataFile;
	}

	public void setTraceDataFile(String traceDataFile) {
		this.traceDataFile = traceDataFile;
	}
}