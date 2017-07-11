package kkr.ktm.domains.common.components.diffmanager.multi;

import java.util.ArrayList;
import java.util.Collection;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;

public abstract class DiffManagerMultiFwk {
	private boolean configured;

	protected Collection<DiffManager> diffManagers;

	public void config() throws ConfigurationException {
		configured = false;
		if (diffManagers == null) {
			diffManagers = new ArrayList<DiffManager>();
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Collection<DiffManager> getDiffManagers() {
		return diffManagers;
	}

	public void setDiffManagers(Collection<DiffManager> diffManagers) {
		this.diffManagers = diffManagers;
	}
}
