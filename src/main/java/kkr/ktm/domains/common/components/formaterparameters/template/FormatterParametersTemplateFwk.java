package kkr.ktm.domains.common.components.formaterparameters.template;

import kkr.ktm.exception.BaseException;



/**
 * FormatterParametersTemplateFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class FormatterParametersTemplateFwk {
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
