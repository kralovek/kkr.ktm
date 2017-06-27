package kkr.ktm.components.testsinitializer;

import java.util.List;
import java.util.Map;

import kkr.ktm.data.TestInput;
import kkr.ktm.exception.BaseException;

public interface TestsInitializer {

	void initialize(List<TestInput> testInputs, Map<String, Object> commonData) throws BaseException;
}
