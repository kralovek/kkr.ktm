package kkr.ktm.components.batchreporter.textqc;

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

public class BatchReporterTextQC extends BatchReporterTextQCFwk implements
		BatchReporter {
	private static final Logger LOG = Logger
			.getLogger(BatchReporterTextQC.class);

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

	private void reportSource(String batchId, String source,
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

				reportSource(source, testReports, ps);

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

	private boolean isIgnoredTest(TestReport testReport) {
		if (patternIgnorName != null && testReport.getName() != null) {
			boolean match = patternIgnorName.matcher(testReport.getName())
					.matches();
			return match;
		}
		return false;
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

	private File generateFilename(String batchId, String source)
			throws BaseException {
		File file = new File(source);
		String filename = file.getName();
		int pos = filename.lastIndexOf('.');
		if (pos != -1) {
			filename = filename.substring(0, pos);
		}
		return new File(dir, batchId + "_" + filename + "_report_QC.txt");
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

	private void reportSource(String source, List<TestReport> testReports,
			PrintStream ps) {

		Stat stat = countResults(testReports);

		if (printResults) {
			ps.println("ALL TESTS: " + stat.all);
			ps.println(" KO TESTS: " + stat.ko);
			ps.println();
		}

		for (TestReport testReport : testReports) {
			if (isIgnoredTest(testReport)) {
				continue;
			}

			ps.println("[ID: " + testReport.getId() + "] [Type: "
					+ testReport.getType() + "] [Source: "
					+ testReport.getSource() + "]");
			ps.println("\tNAME: " + normalizeName(testReport.getName()));
			String description = normalizeDescription(testReport
					.getDescription());
			if (!description.isEmpty()) {
				ps.println("\tDESC: " + description);
			}
			if (printResults && testReport.getOk() != null) {
				ps.println("\tRESULT: " + (testReport.getOk() ? "OK" : "KO"));
			}
			ps.println();
		}
	}

	private String normalizeName(String name) {
		if (name == null) {
			return "";
		}
		return name.trim().replaceAll("\n", " ").replace("\r", "");
	}

	private String normalizeDescription(String description) {
		if (description == null) {
			return "";
		}
		description = description.trim();
		if (description.isEmpty()) {
			return "";
		}
		return description.trim().replaceAll("(^\n|\n$)", "")
				.replaceAll("\n", "\n\t      ");
	}
}
