package kkr.ktm.domains.orchestrator.components.testlancer;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.data.TestOutput;

public interface TestLancer {
	TestOutput lanceTest(TestInput testInput) throws BaseException;
}
