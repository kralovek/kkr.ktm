package kkr.ktm.components.batchreporter.junitxml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import kkr.ktm.components.batchreporter.BatchReporter;
import kkr.ktm.components.batchreporter.TestReport;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsFile;

public class BatchReporterJUnitXml extends BatchReporterJUnitXmlFwk implements
		BatchReporter {
	private static final Logger LOG = Logger
			.getLogger(BatchReporterJUnitXml.class);

	private static final class Stat {
		int all;
		int ko;
	}

	public void report(String batchId, List<TestReport> testReports)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Map<String, List<TestReport>> mapSources = groupBySource(testReports);

			for (Map.Entry<String, List<TestReport>> entrySource : mapSources
					.entrySet()) {
				reportSource(batchId, entrySource.getKey(),
						entrySource.getValue());
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void reportSource(String batchId, String source,
			List<TestReport> testReports) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			File file = generateFilename(batchId, source);
			UtilsFile.getInstance().createFileDirectory(file);

			FileOutputStream fos = null;
			PrintStream ps = null;
			try {
				fos = new FileOutputStream(file);
				ps = new PrintStream(file);

				ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
				//ps.println("<testsuites>");
				reportSource(source, testReports, ps);
				//ps.println("</testsuites>");

				ps.close();
				ps = null;

				fos.close();
				fos = null;
			} catch (IOException ex) {
				throw new TechnicalException(
						"Cannot create the batch report file: "
								+ file.getAbsolutePath());
			} finally {
				UtilsFile.getInstance().close(fos);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void reportSource(String source, List<TestReport> testReports,
			PrintStream ps) {

		Stat stat = countResults(testReports);

		StringBuffer bufferSuite = new StringBuffer(" ");
		bufferSuite.append(" ").append("failures").append("=").append('"')
				.append(stat.ko).append('"');
		bufferSuite.append(" ").append("failedtests").append("=").append('"')
				.append(stat.ko).append('"');
		bufferSuite.append(" ").append("tests").append("=").append('"')
				.append(stat.all).append('"');
		bufferSuite.append(" ").append("name").append("=").append('"')
				.append(source).append('"');
		ps.println("\t<unittest-results" + bufferSuite.toString() + ">");

		// Add mandatory tag: properties
		ps.println("\t\t<properties>");
		ps.println("\t\t\t<property name=\"JOB_NAME\" value=\"KTM - Testes NONREG\"/>");
		ps.println("\t\t</properties>");

		for (TestReport testReport : testReports) {
			StringBuffer bufferCase = new StringBuffer(" ");
			bufferCase.append(" ").append("name").append("=").append('"')
					.append(testReport.getId()).append('_').append(testReport.getName()).append('"');

			if (testReport.getType() != null) {
				bufferCase.append(" ").append("suite").append("=")
						.append('"').append(testReport.getType()).append('"');
			}

			if (testReport.getTimeBegin() != null
					&& testReport.getTimeEnd() != null) {
				long sec = (testReport.getTimeEnd().getTime() - testReport
						.getTimeBegin().getTime()) / 1000L;
				bufferCase.append(" ").append("time").append("=").append('"')
						.append(sec).append('"');
			}
			ps.print("\t\t<test" + bufferCase.toString());
			if (testReport.getOk() == null) {
				ps.println(">");
				ps.println("\t\t\t<skipped />");
				ps.println("\t\t</test>");
			} else if (!testReport.getOk()) {
				ps.println(">");
				ps.println("\t\t\t<failure message=\"TO DO\" type=\"java.lang.Error\">KO</failure>");
				ps.println("\t\t</test>");
			} else {
				ps.println("/>");
			}
		}
		ps.println("\t</unittest-results>");
	}

	private File generateFilename(String batchId, String source)
			throws BaseException {
		File file = new File(source);
		String filename = file.getName();
		int pos = filename.lastIndexOf('.');
		if (pos != -1) {
			filename = filename.substring(0, pos);
		}
		return new File(dir, batchId + "_" + filename + "_report_JUnit.xml");
	}

	private Stat countResults(List<TestReport> testReports) {
		Stat stat = new Stat();
		for (TestReport testReport : testReports) {
			if (testReport.getOk() == null) {
				continue;
			}
			stat.all++;
			stat.ko += !testReport.getOk() ? 1 : 0;
		}
		return stat;
	}

	private Map<String, List<TestReport>> groupBySource(
			List<TestReport> testReports) {
		Map<String, List<TestReport>> mapSources = new TreeMap<String, List<TestReport>>();

		for (TestReport testReport : testReports) {
			List<TestReport> sourceTestReports = mapSources.get(testReport
					.getSource());
			if (sourceTestReports == null) {
				sourceTestReports = new ArrayList<TestReport>();
				mapSources.put(testReport.getSource(), sourceTestReports);
			}
			sourceTestReports.add(testReport);
		}
		return mapSources;
	}
}
