package kkr.ktm.domains.tests.components.testreporter.excelsession;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.tests.components.testreporter.excel.TestReporterExcel;

public class TestReporterExcelSessionFwk extends TestReporterExcel {

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
