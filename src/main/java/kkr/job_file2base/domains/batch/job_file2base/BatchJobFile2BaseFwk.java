package kkr.job_file2base.domains.batch.job_file2base;

import java.io.File;

import kkr.ktm.exception.ConfigurationException;

public abstract class BatchJobFile2BaseFwk {
	private boolean configured;

	protected File dir;

	public void config() throws ConfigurationException {
		configured = false;
		if (dir == null) {
			throw new ConfigurationException("Parameter 'dir' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}
}
