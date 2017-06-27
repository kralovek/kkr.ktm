package kkr.ktm.components.resultparser.soap;

import kkr.ktm.components.resultparser.xml.ResultParserXml;
import kkr.ktm.exception.BaseException;


public abstract class ResultParserSoapFwk extends ResultParserXml {
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
