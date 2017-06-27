package kkr.ktm.domains.tests.components.testreporter.excel;

import java.util.HashMap;
import java.util.Map;

import kkr.ktm.domains.excel.data.TestExcel;
import kkr.ktm.domains.tests.data.TestOutput;

public class TestOutputExcel extends TestExcel implements TestOutput {

	private Map<String, Object> dataOutput = new HashMap<String, Object>();

	public Map<String, Object> getDataOutput() {
		return dataOutput;
	}
}
