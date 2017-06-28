package kkr.ktm.domains.common.components.parserparameters.soap;

import kkr.ktm.domains.common.components.parserparameters.xml.ParserParametersXml;
import kkr.ktm.exception.BaseException;


public abstract class ParserParametersSoapFwk extends ParserParametersXml {
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
