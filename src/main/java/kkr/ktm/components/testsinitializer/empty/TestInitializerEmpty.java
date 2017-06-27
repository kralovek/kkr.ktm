package kkr.ktm.components.testsinitializer.empty;

import java.util.List;
import java.util.Map;

import kkr.ktm.components.testsinitializer.TestsInitializer;
import kkr.ktm.data.TestInput;
import kkr.ktm.exception.BaseException;

public class TestInitializerEmpty extends TestInitializerEmptyFwk implements TestsInitializer {

	public void initialize(List<TestInput> testInputs, Map<String, Object> commonData) throws BaseException {
	}
}
