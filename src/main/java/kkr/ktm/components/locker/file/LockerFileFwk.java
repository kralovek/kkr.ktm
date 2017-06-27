package kkr.ktm.components.locker.file;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kkr.ktm.exception.ConfigurationException;


public class LockerFileFwk {
	private boolean configured;
	protected File dir;
	protected File file;
	protected String whoami;
	private String whoamiFilter;
	protected Pattern whoamiFilterPattern;
	protected Long waitMax;
	protected Long waitInterval;
	protected File stopFile;

	public void config() throws ConfigurationException {
		configured = false;
		if (whoami == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter whoami is not configured");
		} else if (whoami.isEmpty()) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter whoami may not be empty");
		} else {
			if (!whoami.matches("[a-zA-Z0-9_\\-.]{1,}")) {
				throw new ConfigurationException(
						getClass().getSimpleName()
								+ ": Parameter whoami has bad value. It must match the pattern: [a-zA-Z0-9_\\-.]{1,} : " + whoami);
			}
		}
		if (whoamiFilter != null) {
			try {
				whoamiFilterPattern = Pattern.compile(whoamiFilter);
			} catch (PatternSyntaxException ex) {
				throw new ConfigurationException(
						getClass().getSimpleName()
								+ ": Parameter whoamiFilter has bad value. It is not a regexp pattern: " + whoamiFilter);
			}
		} else {
			whoamiFilterPattern = Pattern.compile(".*\\.lock");
		}
		if (dir == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter dir is not configured");
		} else {
			file = new File(dir, whoami + ".lock");
		}
		if (waitMax == null) {
			// OK
		}
		if (waitInterval == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter waitInterval is not configured");
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

	public String getWhoami() {
		return whoami;
	}

	public void setWhoami(String whoami) {
		this.whoami = whoami;
	}

	public Long getWaitMax() {
		return waitMax;
	}

	public void setWaitMax(Long waitMax) {
		this.waitMax = waitMax;
	}

	public Long getWaitInterval() {
		return waitInterval;
	}

	public void setWaitInterval(Long waitInterval) {
		this.waitInterval = waitInterval;
	}

	public File getStopFile() {
		return stopFile;
	}

	public void setStopFile(File stopFile) {
		this.stopFile = stopFile;
	}

	public String getWhoamiFilter() {
		return whoamiFilter;
	}

	public void setWhoamiFilter(String whoamiFilter) {
		this.whoamiFilter = whoamiFilter;
	}
}
