package kkr.ktm.domains.common.components.formatter.auto;

import java.util.Locale;

import kkr.common.errors.ConfigurationException;

public abstract class FormatterAutoFwk {
	private boolean configured;

	protected static final Locale LOCALE = Locale.US;

	public void config() throws ConfigurationException {
		configured = false;
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}
}
