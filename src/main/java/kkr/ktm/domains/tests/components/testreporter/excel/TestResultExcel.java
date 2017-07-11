package kkr.ktm.domains.tests.components.testreporter.excel;

import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestBase;
import kkr.ktm.domains.tests.data.TestResult;

public class TestResultExcel extends TestBase implements TestResult {

	private Status status;

	public TestResultExcel(Test test) {
		super(test);
	}

	public TestResultExcel(String name, String description, String source, String type, String code, Integer group) {
		super(name, description, source, type, code, group);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status result) {
		this.status = result;
	}
}
