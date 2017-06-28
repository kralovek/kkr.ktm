package kkr.ktm.components.filemanager.ftp_base;

import kkr.ktm.exception.ConfigurationException;

public abstract class FileManagerFtpBaseFwk {
	private boolean configured;

	private static final int DEFAULT_PORT = 21;

	protected String ftpHost;
	protected Integer ftpPort;
	protected String ftpLogin;
	protected String ftpPassword;

	protected String traceDataFile;

	public void config() throws ConfigurationException {
		configured = false;
		if (ftpHost == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter ftpHost is not configured");
		}
		if (ftpPort == null) {
			ftpPort = DEFAULT_PORT;
		}
		if (ftpLogin == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter ftpLogin is not configured");
		}
		if (ftpPassword == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter ftpPassword is not configured");
		}
		if (traceDataFile == null) {
			// OK
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpLogin() {
		return ftpLogin;
	}

	public void setFtpLogin(String ftpLogin) {
		this.ftpLogin = ftpLogin;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getTraceDataFile() {
		return traceDataFile;
	}

	public void setTraceDataFile(String traceDataFile) {
		this.traceDataFile = traceDataFile;
	}
}
