package kkr.ktm.domains.tests.components.testreporter.excelsession;

import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.tests.components.testreporter.TestReporterSession;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.ktm.domains.tests.data.TestResult;

public class TestReporterExcelSession extends TestReporterExcelSessionFwk implements TestReporterSession {
	private static final Logger LOG = Logger.getLogger(TestReporterExcelSession.class);

	public void open(String target) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void close() throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

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
		} finally {
			LOG.trace("END");
		}

		return null;
	}

	public Collection<TestResult> loadResults(String source, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
		return null;
	}

}
