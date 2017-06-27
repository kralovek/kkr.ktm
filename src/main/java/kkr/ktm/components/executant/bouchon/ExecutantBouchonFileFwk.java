package kkr.ktm.components.executant.bouchon;


import java.io.File;

import kkr.ktm.exception.BaseException;


/**
 * ExecutantBouchonFileFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class ExecutantBouchonFileFwk {
    private boolean configured;

	protected File file;

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

	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[").append(this.getClass().getName()).append("]\n");
		buffer.append("    ").append("file").append("=").append(
				file == null ? "" : file.getAbsolutePath()).append("\n");
		return buffer.toString();
	}

	public File getFile() {
		return file;
	}

	public void setFile(final File pFileResponse) {
		this.file = pFileResponse;
	}
}
