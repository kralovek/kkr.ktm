package kkr.ktm.domains.common.components.parametersformater.template;

import kkr.common.errors.BaseException;



/**
 * ParametersFormatterTemplateFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class ParametersFormatterTemplateFwk {
    private boolean configured;

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
		return buffer.toString();
	}
}
