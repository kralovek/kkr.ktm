package kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.base;

import java.util.ArrayList;
import java.util.List;

import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DirInfo;

public abstract class DiffManagerFtpBaseFwk implements DiffManager {
	protected static final String PATH_SEPARATOR = "/";
	protected static final String UNIX_PATH_SEPARATOR = "/";

	private boolean configured;

	protected String code;
	protected List<DirInfo> dirInfos;

	protected String ftpHost;
	protected Integer ftpPort;
	protected String ftpLogin;
	protected String ftpPassword;

	public void config() throws ConfigurationException {
		configured = false;
		if (dirInfos == null) {
			dirInfos = new ArrayList<DirInfo>();
		} else {
			for (DirInfo dirInfo : dirInfos) {
				if (dirInfo.getPath() == null) {
					throw new ConfigurationException(
							getClass().getSimpleName() + "." + dirInfo.getClass().getSimpleName() + ": Parameter dirPath is not configured");
				}
				if (!dirInfo.getPath().endsWith(PATH_SEPARATOR)) {
					dirInfo.setPath(dirInfo.getPath() + PATH_SEPARATOR);
				}
				if (dirInfo.getName() == null) {
					dirInfo.setName(dirInfo.getPath());
				}
			}
		}
		if (UtilsString.isEmpty(code)) {
			// OK
		} else {
			if (!code.matches("[a-zA-Z_0-9]")) {
				throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'code' has bad value: " + code);
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public List<DirInfo> getDirInfos() {
		return dirInfos;
	}

	public void setDirInfos(List<DirInfo> dirInfos) {
		this.dirInfos = dirInfos;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

}
