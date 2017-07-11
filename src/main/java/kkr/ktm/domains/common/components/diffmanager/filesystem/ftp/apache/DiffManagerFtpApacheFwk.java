package kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.apache;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.base.DiffManagerFtpBase;

public abstract class DiffManagerFtpApacheFwk extends DiffManagerFtpBase {
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
