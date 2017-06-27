package kkr.ktm.components.batchreporter.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import kkr.ktm.components.batchreporter.BatchReporter;
import kkr.ktm.components.batchreporter.TestReport;
import kkr.ktm.exception.BaseException;

public class BatchReporterScreen extends BatchReporterScreenFwk implements
		BatchReporter {
	private static final Logger LOG = Logger
			.getLogger(BatchReporterScreen.class);

	public void report(String batchId, List<TestReport> testReports)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<String, Map<String, List<TestReport>>> mapSources = groupBySourceType(testReports);
			reportByGroup(batchId, mapSources);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
	
	private void reportByGroup(String batchId, Map<String, Map<String, List<TestReport>>> mapSources) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.info("==============================");
			LOG.info("REVIEW Batch [" + batchId + "]");
			LOG.info("==============================");
			int countKoAll = 0;
			int countOkAll = 0;

			for (Map.Entry<String, Map<String, List<TestReport>>> entrySource : mapSources
					.entrySet()) {
				LOG.info("\tSource [" + (String) entrySource.getKey() + "]");
				int countKoSource = 0;
				int countOkSource = 0;
				for (Map.Entry<String, List<TestReport>> entryType : entrySource
						.getValue().entrySet()) {
					LOG.info("\t\tType [" + (String) entryType.getKey()
							+ "]");
					int countKoType = 0;
					int countOkType = 0;
					for (TestReport testReport : entryType.getValue()) {
						if (testReport.getOk() != null) {
							countKoType += (testReport.getOk() ? 0 : 1);
							countOkType += (testReport.getOk() ? 1 : 0);
							if (testReport.getOk())
								LOG.info("\t\t\t * OK *  - Test ["
										+ testReport.getId() + "]");
							else {
								LOG.info("\t\t\t## KO ## - Test ["
										+ testReport.getId() + "]");
							}
						} else {
							LOG.info("\t\t\tTest [" + testReport.getId()
									+ "] : SKIPPED");
						}
					}
					countKoSource += countKoType;
					countOkSource += countOkType;
					if (countKoType == 0)
						LOG.info("\t\t * OK (" + countOkType + ") *");
					else {
						LOG.info("\t\t## KO (" + countKoType + "/"
								+ (countOkType + countKoType) + ") ##");
					}
				}
				countKoAll += countKoSource;
				countOkAll += countOkSource;

				if (countKoSource == 0)
					LOG.info("\t * OK (" + countOkSource + ") *");
				else {
					LOG.info("\t## KO (" + countKoSource + "/"
							+ (countOkSource + countKoSource) + ") ##");
				}
			}
			if (countKoAll == 0) {
				LOG.info("------------------------------");
				LOG.info("* OK (" + countOkAll + ") *");
				LOG.info("All tests finished SUCCESSFULY");
				LOG.info("------------------------------");
			} else {
				LOG.info("##############################");
				LOG.info("# KO (" + countKoAll + "/"
						+ (countOkAll + countKoAll) + ") #");
				LOG.info("Some tests finished in ERROR");
				LOG.info("##############################");
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, Map<String, List<TestReport>>> groupBySourceType(List<TestReport> testReports) {
		Map<String, Map<String, List<TestReport>>> mapSources = new TreeMap<String, Map<String,List<TestReport>>>();
		for (TestReport testReport : testReports) {
			Map<String, List<TestReport>> mapTypes = mapSources.get(testReport.getSource());
			if (mapTypes == null) {
				mapTypes = new TreeMap<String, List<TestReport>>();
				mapSources.put(testReport.getSource(), mapTypes);
			}
			List<TestReport> listTestReports = mapTypes.get(testReport.getType());
			if (listTestReports == null) {
				listTestReports = new ArrayList<TestReport>();
				mapTypes.put(testReport.getType(), listTestReports);
			}
			listTestReports.add(testReport);
		}
		return mapSources;
	}
}
