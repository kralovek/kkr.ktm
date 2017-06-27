package kkr.ktm.components.batchreporter.junitxml;

import java.io.File;
import java.util.regex.Pattern;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;

public abstract class BatchReporterJUnitXmlFwk {
	private boolean configured;

	protected File dir;

	private String maskIgnoreName;
	protected Pattern patternIgnorName;

	public void config() throws BaseException {
		configured = false;
		if (dir == null) {
			throw new ConfigurationException(
					"The parameter 'dir' is not configured.");
		}
		if (maskIgnoreName != null) {
			patternIgnorName = Pattern.compile(maskIgnoreName);
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public String getMaskIgnoreName() {
		return maskIgnoreName;
	}

	public void setMaskIgnoreName(String maskIgnoreName) {
		this.maskIgnoreName = maskIgnoreName;
	}
}
