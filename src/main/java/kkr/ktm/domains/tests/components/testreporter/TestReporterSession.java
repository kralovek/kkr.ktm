package kkr.ktm.domains.tests.components.testreporter;

import kkr.common.errors.BaseException;

public interface TestReporterSession extends TestReporter {

	void open(String target) throws BaseException;

	void close() throws BaseException;
}
