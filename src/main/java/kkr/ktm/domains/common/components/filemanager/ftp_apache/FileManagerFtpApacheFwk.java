package kkr.ktm.domains.common.components.filemanager.ftp_apache;

import kkr.ktm.domains.common.components.filemanager.ftp_base.FileManagerFtpBase;
import kkr.ktm.exception.ConfigurationException;

public abstract class FileManagerFtpApacheFwk extends FileManagerFtpBase {
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
