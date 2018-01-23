package kkr.ktm.domains.tests.components.testreporter.empty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.tests.components.testreporter.TestReporter;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.ktm.domains.tests.data.TestResult;

public class TestReporterEmpty extends TestReporterEmptyFwk implements TestReporter {
	private static final Logger LOG = Logger.getLogger(TestReporterEmpty.class);

	public void skipTest(Test test, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public Status reportTest(TestOutput testOutput, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
			return Status.KO;
		} finally {
			LOG.trace("END");
		}
	}

	public Map<Test, Status> reportTests(Collection<TestOutput> testsOutputs, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<Test, Status> retval = new HashMap<Test, Status>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<TestResult> loadResults(String source, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<TestResult> retval = new ArrayList<TestResult>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
