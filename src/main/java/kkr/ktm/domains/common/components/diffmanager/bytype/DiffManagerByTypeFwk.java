package kkr.ktm.domains.common.components.diffmanager.bytype;

import java.util.Map;
import java.util.regex.Pattern;

import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsConfig;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;

public abstract class DiffManagerByTypeFwk {
	private boolean configured;

	protected Map<String, DiffManager> _diffManagers;
	protected Map<Pattern, DiffManager> diffManagers;

	public void config() throws ConfigurationException {
		configured = false;
		diffManagers = UtilsConfig.checkMapPatterns(_diffManagers, "diffManagers");
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Map<String, DiffManager> getDiffManagers() {
		return _diffManagers;
	}

	public void setDiffManagers(Map<String, DiffManager> diffManagers) {
		this._diffManagers = diffManagers;
	}

}
