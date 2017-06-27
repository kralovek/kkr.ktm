package kkr.ktm.components.batchreporter.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import kkr.ktm.components.batchreporter.BatchReporter;
import kkr.ktm.components.batchreporter.TestReport;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.exception.BaseException;
import kkr.ktm.utils.UtilsFile;

public class BatchReporterExcel extends BatchReporterExcelFwk implements BatchReporter {
	private static final Logger LOG = Logger.getLogger(BatchReporterExcel.class);

	private static final class Stat {
		int all;
		int ko;
	}

	public void report(String batchId, List<TestReport> testReports) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			Map<String, List<TestReport>> mapSources = groupBySource(testReports);

			for (Map.Entry<String, List<TestReport>> entrySource : mapSources.entrySet()) {
				reportSource(batchId, entrySource.getKey(), entrySource.getValue());
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, List<TestReport>> groupBySource(List<TestReport> testReports) {
		Map<String, List<TestReport>> mapSources = new TreeMap<String, List<TestReport>>();

		for (TestReport testReport : testReports) {
			List<TestReport> sourceTestReports = mapSources.get(testReport.getSource());
			if (sourceTestReports == null) {
				sourceTestReports = new ArrayList<TestReport>();
				mapSources.put(testReport.getSource(), sourceTestReports);
			}
			sourceTestReports.add(testReport);
		}
		return mapSources;
	}

	private void reportSource(String batchId, String source, List<TestReport> testReports) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			File file = generateFilename(batchId, source);
			UtilsFile.getInstance().createFileDirectory(file);

			TWorkbook tWorkbook = excelAdapter.createWorkbook(file);

			reportSource(source, testReports, tWorkbook);

			excelAdapter.saveWorkbook(tWorkbook);
			excelAdapter.closeWorkbook(tWorkbook);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void reportSource(String source, List<TestReport> testReports, TWorkbook tWorkbook) {

		TSheet tSheet = excelAdapter.createSheet(tWorkbook, "RESULTS");

		Stat stat = countResults(testReports);

		printResume(tWorkbook, tSheet, stat);

		printHeader(tWorkbook, tSheet);

		int irow = 5;
		for (TestReport testReport : testReports) {
			if (isIgnoredTest(testReport)) {
				continue;
			}
			printLine(tWorkbook, tSheet, testReport, irow++);
		}
	}

	private void printResume(TWorkbook tWorkbook, TSheet tSheet, Stat stat) {
		TCell tCell;
		if (printResults) {
			tCell = excelAdapter.createCell(tSheet, 1, 0);
			excelAdapter.setValue(tCell, "ALL TESTS");
			tCell = excelAdapter.createCell(tSheet, 1, 1);
			excelAdapter.setValue(tCell, stat.all);
			tCell = excelAdapter.createCell(tSheet, 2, 0);
			excelAdapter.setValue(tCell, "KO TESTS");
			tCell = excelAdapter.createCell(tSheet, 2, 1);
			excelAdapter.setValue(tCell, stat.ko);
		}
	}

	private void printHeader(TWorkbook tWorkbook, TSheet tSheet) {
		int index = 0;
		TCell tCell;
		tCell = excelAdapter.createCell(tSheet, 4, index++);
		excelAdapter.setValue(tCell, "ID");
		tCell = excelAdapter.createCell(tSheet, 4, index++);
		excelAdapter.setValue(tCell, "TYPE");
		tCell = excelAdapter.createCell(tSheet, 4, index++);
		excelAdapter.setValue(tCell, "SOURCE");
		if (printResults) {
			tCell = excelAdapter.createCell(tSheet, 4, index++);
			excelAdapter.setValue(tCell, "RESULT");
		}
		tCell = excelAdapter.createCell(tSheet, 4, index++);
		excelAdapter.setValue(tCell, "NAME");
		tCell = excelAdapter.createCell(tSheet, 4, index++);
		excelAdapter.setValue(tCell, "DESCRIPTION");
	}

	private void printLine(TWorkbook tWorkbook, TSheet tSheet, TestReport testReport, int irow) {

		int index = 0;
		TCell tCell;

		tCell = excelAdapter.createCell(tSheet, irow, index++);
		excelAdapter.setValue(tCell, testReport.getId());
		tCell = excelAdapter.createCell(tSheet, irow, index++);
		excelAdapter.setValue(tCell, testReport.getType());
		tCell = excelAdapter.createCell(tSheet, irow, index++);
		excelAdapter.setValue(tCell, testReport.getSource());
		if (printResults) {
			tCell = excelAdapter.createCell(tSheet, irow, index++);
			String value = testReport.getOk() != null ? (testReport.getOk() ? "OK" : "KO") : "SKIPPED";
			excelAdapter.setValue(tCell, value);
		}
		tCell = excelAdapter.createCell(tSheet, irow, index++);
		excelAdapter.setValue(tCell, normalizeName(testReport.getName()));
		tCell = excelAdapter.createCell(tSheet, irow, index++);
		excelAdapter.setValue(tCell, normalizeDescription(testReport.getDescription()));
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
		return description.trim().replaceAll("(^\n|\n$)", "").replaceAll("\n", "\n\t      ");
	}

	private boolean isIgnoredTest(TestReport testReport) {
		if (patternIgnorName != null && testReport.getName() != null) {
			boolean match = patternIgnorName.matcher(testReport.getName()).matches();
			return match;
		}
		return false;
	}

	private File generateFilename(String batchId, String source) throws BaseException {
		File file = new File(source);
		String filename = file.getName();
		int pos = filename.lastIndexOf('.');
		if (pos != -1) {
			filename = filename.substring(0, pos);
		}
		return new File(dir, batchId + "_" + filename + "_report_excel.xlsx");
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
}
