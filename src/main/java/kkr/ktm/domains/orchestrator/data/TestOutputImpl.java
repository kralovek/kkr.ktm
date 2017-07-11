package kkr.ktm.domains.orchestrator.data;

import java.util.HashMap;
import java.util.Map;

import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestBase;
import kkr.ktm.domains.tests.data.TestOutput;

public class TestOutputImpl extends TestBase implements TestOutput {
	private Map<String, Object> dataOutput = new HashMap<String, Object>();

	public TestOutputImpl(Test test) {
		super(test);
	}

	public TestOutputImpl(String name, String description, String source, String type, String code, Integer group) {
		super(name, description, source, type, code, group);
	}

	public Map<String, Object> getDataOutput() {
		return dataOutput;
	}
}
