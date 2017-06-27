package kkr.ktm.domains.tests.components.testreporter.excel;

import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.excel.data.TestExcel;
import kkr.ktm.domains.tests.data.TestResult;

public class TestResultExcel extends TestExcel implements TestResult {

	private Status status;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status result) {
		this.status = result;
	}
}
