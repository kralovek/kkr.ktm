package kkr.ktm.domains.common.components.parametersparser.soap;

import kkr.ktm.domains.common.components.parametersparser.xml.ParametersParserXml;
import kkr.common.errors.BaseException;


public abstract class ParametersParserSoapFwk extends ParametersParserXml {
    private boolean configured;
    
	public void config() throws BaseException {
		configured = false;
		super.config();
		configured = true;
	}

	public void testConfigured() {
		super.testConfigured();
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}
}
