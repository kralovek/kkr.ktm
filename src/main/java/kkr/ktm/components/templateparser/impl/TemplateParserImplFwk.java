package kkr.ktm.components.templateparser.impl;

import kkr.ktm.exception.BaseException;



/**
 * TemplateParserImplFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class TemplateParserImplFwk {
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
