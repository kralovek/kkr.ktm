package kkr.ktm.domains.tests.components.testreporter;

import java.util.Collection;
import java.util.Map;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.ktm.domains.tests.data.TestResult;

public interface TestReporter {

	void skipTest(Test test, String batchId) throws BaseException;

	Status reportTest(TestOutput testOutput, String batchId) throws BaseException;

	Map<Test, Status> reportTests(Collection<TestOutput> testsOutputs, String batchId) throws BaseException;

	Collection<TestResult> loadResults(String source, String batchId) throws BaseException;
}
