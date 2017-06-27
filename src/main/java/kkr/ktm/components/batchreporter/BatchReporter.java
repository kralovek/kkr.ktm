package kkr.ktm.components.batchreporter;

import java.util.List;

import kkr.ktm.exception.BaseException;

public interface BatchReporter {

	void report(String batchId, List<TestReport> testReports)
			throws BaseException;
}
