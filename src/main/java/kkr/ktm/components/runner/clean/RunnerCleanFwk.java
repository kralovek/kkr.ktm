package kkr.ktm.components.runner.clean;

import java.util.ArrayList;
import java.util.List;

import kkr.ktm.components.cleanmanager.CleanManager;
import kkr.ktm.exception.ConfigurationException;


public abstract class RunnerCleanFwk {
	private boolean configured;

	protected List<CleanManager> cleanManagers;
    protected String sysParamPrefix;

    public void config() throws ConfigurationException {
		configured = false;
		if (cleanManagers == null) {
			cleanManagers = new ArrayList<CleanManager>();
		}
		if (sysParamPrefix == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter sysPrefix is not configured");
		} else if (!sysParamPrefix.isEmpty() && !sysParamPrefix.endsWith("/")) {
			sysParamPrefix += "/";
		}
		configured = true;
    }
    
	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}

	public List<CleanManager> getCleanManagers() {
		return cleanManagers;
	}

	public void setCleanManagers(List<CleanManager> cleanManagers) {
		this.cleanManagers = cleanManagers;
	}
}
