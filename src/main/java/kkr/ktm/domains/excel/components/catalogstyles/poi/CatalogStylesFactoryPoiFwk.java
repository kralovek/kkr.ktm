package kkr.ktm.domains.excel.components.catalogstyles.poi;

import kkr.ktm.exception.ConfigurationException;

public abstract class CatalogStylesFactoryPoiFwk extends CatalogStylesPoiFwk {
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
