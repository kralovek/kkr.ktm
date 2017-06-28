package kkr.ktm.components.filemanager.sftp_jsch;

import kkr.ktm.components.filemanager.ftp_base.FileManagerFtpBase;
import kkr.ktm.exception.ConfigurationException;

public abstract class FileManagerSFtpJschFwk extends FileManagerFtpBase {
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
