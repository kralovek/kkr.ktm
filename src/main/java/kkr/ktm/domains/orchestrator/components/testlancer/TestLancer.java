package kkr.ktm.domains.orchestrator.components.testlancer;

import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.common.errors.BaseException;

public interface TestLancer {

	TestOutput lanceTest(TestInput testInput) throws BaseException;
}
