package kkr.ktm.domains.tests.components.testreporter.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.errors.ExcelException;
import kkr.common.errors.TechnicalException;
import kkr.common.utils.UtilsFile;
import kkr.common.utils.UtilsString;
import kkr.common.utils.excel.ExcelPosition;
import kkr.ktm.domains.excel.components.catalogstyles.CatalogStyles;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TStyle;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.domains.excel.data.ExcelIdCell;
import kkr.ktm.domains.excel.data.KtmStyle;
import kkr.ktm.domains.excel.data.Orientation;
import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.excel.data.StructureParameter;
import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureTest;
import kkr.ktm.domains.excel.data.StructureWorkbook;
import kkr.ktm.domains.tests.components.testreporter.TestReporter;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.ktm.domains.tests.data.TestResult;
import kkr.ktm.domains.tests.data.ValuePattern;

public class TestReporterExcel extends TestReporterExcelFwk implements TestReporter {
	private static final Logger LOG = Logger.getLogger(TestReporterExcel.class);

	public void skipTest(Test test, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			workReport(test, batchId, true);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public Status reportTest(TestOutput testOutput, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<TestOutput> testsOutput = new ArrayList<TestOutput>();
			testsOutput.add(testOutput);
			Map<Test, Status> statuses = workReport(testsOutput, batchId, false);
			Status status = statuses.values().iterator().next();
			LOG.trace("OK");
			return status;
		} finally {
			LOG.trace("END");
		}
	}

	public Map<Test, Status> reportTests(Collection<TestOutput> testsOutputs, String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<Test, Status> retval = new HashMap<Test, Status>();
			Map<String, Collection<TestOutput>> testsBySource = new LinkedHashMap<String, Collection<TestOutput>>();

			for (TestOutput testOutput : testsOutputs) {
				Collection<TestOutput> tests = testsBySource.get(testOutput.getSource());
				if (tests == null) {
					tests = new ArrayList<TestOutput>();
					testsBySource.put(testOutput.getSource(), tests);
				}
				tests.add(testOutput);
			}

			for (Map.Entry<String, Collection<TestOutput>> entry : testsBySource.entrySet()) {
				Map<Test, Status> statuses = workReport(entry.getValue(), batchId, false);
				retval.putAll(statuses);
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Map<Test, Status> workReport(Collection<TestOutput> tests, String batchId, boolean skip)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<Test, Status> retval = new HashMap<Test, Status>();
			if (tests.isEmpty()) {
				LOG.trace("OK");
				return retval;
			}

			Test firstTest = tests.iterator().next();

			File fileSource = new File(firstTest.getSource());
			File fileTarget = generateTargetFile(firstTest.getSource(), batchId);
			File fileTmp = generateTempFile(fileTarget);

			TWorkbook workbookSource;
			if (fileTarget.exists()) {
				workbookSource = excelAdapter.readWorkbook(fileTarget);
			} else {
				workbookSource = excelAdapter.readWorkbook(fileSource);
			}
			TWorkbook workbookTarget = excelAdapter.cloneWorkbook(workbookSource, fileTmp);

			ExcelPosition excelPositionWorkbook = new ExcelPosition();
			excelPositionWorkbook.setFile(workbookTarget.getFile());

			excelAdapter.closeWorkbook(workbookSource);

			CatalogStyles catalogStyles = catalogStylesFactory.createInstance(workbookTarget);

			for (Test test : tests) {
				Status status = workWorkbook(excelPositionWorkbook, workbookTarget, test, catalogStyles, skip);

				workReview(excelPositionWorkbook, workbookTarget, test, status, catalogStyles);

				retval.put(test, status);
			}

			excelAdapter.saveWorkbook(workbookTarget);

			excelAdapter.closeWorkbook(workbookTarget);

			renameFile(fileTmp, fileTarget);

			catalogStyles.close();

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Status workReport(Test test, String batchId, boolean skip) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File fileSource = new File(test.getSource());
			File fileTarget = generateTargetFile(test.getSource(), batchId);
			File fileTmp = generateTempFile(fileTarget);

			TWorkbook workbookSource;
			if (fileTarget.exists()) {
				workbookSource = excelAdapter.readWorkbook(fileTarget);
			} else {
				workbookSource = excelAdapter.readWorkbook(fileSource);
			}
			TWorkbook workbookTarget = excelAdapter.cloneWorkbook(workbookSource, fileTmp);

			ExcelPosition excelPositionWorkbook = new ExcelPosition();
			excelPositionWorkbook.setFile(workbookTarget.getFile());

			excelAdapter.closeWorkbook(workbookSource);

			CatalogStyles catalogStyles = catalogStylesFactory.createInstance(workbookTarget);

			Status status = workWorkbook(excelPositionWorkbook, workbookTarget, test, catalogStyles, skip);

			workReview(excelPositionWorkbook, workbookTarget, test, status, catalogStyles);

			excelAdapter.saveWorkbook(workbookTarget);

			excelAdapter.closeWorkbook(workbookTarget);

			renameFile(fileTmp, fileTarget);

			catalogStyles.close();

			LOG.trace("OK");
			return status;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<TestResult> loadResults(String source, String batchID) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<TestResult> retval = new ArrayList<TestResult>();

			File file = generateTargetFile(source, batchID);

			TWorkbook tWorkbook = null;
			try {
				tWorkbook = excelAdapter.readWorkbook(file);
				ExcelPosition excelPosition = new ExcelPosition();
				excelPosition.setFile(file);

				StructureWorkbook structureWorkbook = structureLoader.loadStructureWorkbook(excelPosition, tWorkbook);

				int sheetCount = excelAdapter.getSheetCount(tWorkbook);
				for (int i = 0; i < sheetCount; i++) {
					TSheet tSheet = excelAdapter.getSheet(tWorkbook, i);

					StructureSheet structureSheet = structureWorkbook.getSheets().get(tSheet.getName());

					if (structureSheet == null) {
						LOG.info("Sheet ignored: " + tSheet.getName());
						continue;
					}

					Iterator<StructureTest> iteratorT = structureSheet.iteratorTests();
					while (iteratorT.hasNext()) {
						StructureTest structureTest = iteratorT.next();
						if (structureTest.getStatus() == null) {
							// test non executed
							continue;
						}
						TestResultExcel testResult = new TestResultExcel(//
								structureTest.getName(), //
								structureTest.getDescription(), //
								source, //
								tSheet.getName(), //
								structureTest.getCode(), //
								structureTest.getGroup());

						testResult.setStatus(structureTest.getStatus());

						retval.add(testResult);
					}
				}

				excelAdapter.closeWorkbook(tWorkbook);
				tWorkbook = null;

				LOG.trace("OK");
				return retval;
			} finally {
				try {
					if (tWorkbook != null) {
						excelAdapter.closeWorkbook(tWorkbook);
					}
				} catch (Exception ex) {
					// nothing to do
				}
			}
		} finally {
			LOG.trace("END");
		}
	}

	private File generateTargetFile(String source, String batchID) {
		File fileSource = new File(source);
		return new File(dir, (batchID != null ? batchID : "XXX") + "_" + fileSource.getName());
	}

	private File generateTempFile(File filetarget) {
		File fileTmp = new File(filetarget.getParentFile(), "TMP_" + filetarget.getName());
		return fileTmp;
	}

	private void renameFile(File fileSource, File fileTarget) throws BaseException {
		UtilsFile.createFileDirectory(fileTarget);
		if (fileTarget.exists() && !fileTarget.delete()) {
			throw new TechnicalException("Cannot delete the file '" + fileSource.getAbsolutePath() + "' to '"
					+ fileTarget.getAbsolutePath() + "'");
		}
		if (!fileSource.renameTo(fileTarget)) {
			throw new TechnicalException("Cannot rename the temp file '" + fileSource.getAbsolutePath() + "' to '"
					+ fileTarget.getAbsolutePath() + "'");
		}
	}

	private Status workWorkbook(ExcelPosition excelPositionWorkbook, TWorkbook tWorkbook, Test test,
			CatalogStyles catalogStyles, boolean skip) throws BaseException {
		LOG.trace("BEGIN");
		try {
			TSheet tSheet = excelAdapter.getSheet(tWorkbook, test.getType());
			if (tSheet == null) {
				throw new ExcelException(excelPositionWorkbook,
						"The excel file does not contain a sheet: " + test.getType());
			}

			Status status = workSheet(excelPositionWorkbook, tWorkbook, tSheet, test, catalogStyles, skip);

			LOG.trace("OK");
			return status;
		} finally {
			LOG.trace("END");
		}
	}

	private Status workSheet(ExcelPosition excelPositionWorkbook, TWorkbook tWorkbook, TSheet tSheet, Test test,
			CatalogStyles catalogStyles, boolean skip) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Status status = Status.OK;

			ExcelPosition excelPositionSheet = excelPositionWorkbook.clone();
			excelPositionSheet.setSheet(tSheet.getName());

			StructureSheet structureSheet = structureLoader.loadStructureSheet(excelPositionSheet, tSheet);

			StructureTest structureTest = structureSheet.findTest(test.getCode());
			if (structureTest == null) {
				throw new ExcelException(excelPositionSheet, "Cannot find the test: " + test.getCode());
			}

			if (!skip) {
				TestOutput testOutput = (TestOutput) test;
				ExcelPosition excelPositionCell = excelPositionSheet.clone();

				Iterator<StructureParameter> iteratorO = structureSheet.iteratorParametersO();
				while (iteratorO.hasNext()) {
					StructureParameter structureParameterO = iteratorO.next();
					updateExcelPosition(excelPositionCell, structureTest.getIndex(), structureParameterO.getIndex(),
							structureSheet.getOrientation());

					TCell tCellO = loadCell(tSheet, structureTest.getIndex(), structureParameterO.getIndex(),
							structureSheet.getOrientation());
					Object valueSourceO = testOutput.getDataOutput().get(structureParameterO.getName());
					Object valueTargetO = valueGenerator.formatValue(excelPositionCell, valueSourceO);

					StructureParameter parameterInfoE = structureSheet.findParameterE(structureParameterO.getName());
					if (parameterInfoE != null) {
						ExcelPosition excelPositionCellE = excelPositionCell.clone();
						updateExcelPosition(excelPositionCellE, structureTest.getIndex(), parameterInfoE.getIndex(),
								structureSheet.getOrientation());
						TCell tCellE = loadCell(tSheet, structureTest.getIndex(), parameterInfoE.getIndex(),
								structureSheet.getOrientation());

						boolean resultValue = writeValue(excelPositionCell, tWorkbook, tSheet, tCellO, tCellE,
								valueSourceO, valueTargetO, catalogStyles);
						if (!resultValue) {
							status = Status.KO;
						}
					} else {
						writeValue(excelPositionCell, tCellO, valueTargetO, catalogStyles);
					}
				}
			} else {
				status = Status.SKIP;
			}

			writeStatusTest(excelPositionSheet, tSheet, structureSheet, structureTest, status, catalogStyles);
			writeStatusSheet(excelPositionSheet, tSheet, structureSheet, status, catalogStyles);

			LOG.trace("OK");
			;
			return status;
		} finally {
			LOG.trace("END");
		}
	}

	private void writeValue(ExcelPosition excelPositionCell, TCell tCellO, Object value, CatalogStyles catalogStyles)
			throws BaseException {
		excelAdapter.setValue(tCellO, value);
		applyStyle(tCellO, KtmStyle.OUTPUT, catalogStyles);
	}

	private boolean writeValue(ExcelPosition excelPositionCell, TWorkbook tWorkbook, TSheet tSheet, TCell tCellO,
			TCell tCellE, Object valueSourceO, Object valueTargetO, CatalogStyles catalogStyles) throws BaseException {
		Object valueSourceE = excelAdapter.getValue(tCellE);
		ValuePattern patternSourceE = valueGenerator.parsePattern(excelPositionCell, valueSourceE);

		boolean result = valueGenerator.compareValues(excelPositionCell, patternSourceE, valueSourceO);

		excelAdapter.setValue(tCellO, valueTargetO);

		if (result) {
			applyStyle(tCellE, KtmStyle.EXPECTED_OK, catalogStyles);
			applyStyle(tCellO, KtmStyle.OUTPUT_OK, catalogStyles);
		} else {
			applyStyle(tCellE, KtmStyle.EXPECTED_KO, catalogStyles);
			applyStyle(tCellO, KtmStyle.OUTPUT_KO, catalogStyles);
		}

		return result;
	}

	private void writeStatusTest(ExcelPosition excelPositionSheet, TSheet tSheet, StructureSheet structureSheet,
			StructureTest structureTest, Status status, CatalogStyles catalogStyles) throws BaseException {
		TCell tCellRt = loadCell(tSheet, structureTest.getIndex(), structureSheet.getIndexStatus(),
				structureSheet.getOrientation());

		switch (status) {
		case OK:
			excelAdapter.setValue(tCellRt, Status.OK.name());
			applyStyle(tCellRt, KtmStyle.OK, catalogStyles);
			break;

		case KO:
			excelAdapter.setValue(tCellRt, Status.KO.name());
			applyStyle(tCellRt, KtmStyle.KO, catalogStyles);
			break;

		case SKIP:
			excelAdapter.setValue(tCellRt, Status.SKIP.name());
			applyStyle(tCellRt, KtmStyle.SKIP, catalogStyles);
			break;
		default:
			throw new IllegalArgumentException("Unsupported DiffStatus: " + status);
		}
	}

	private KtmStyle statusToStyle(Status status) {
		switch (status) {
		case OK:
			return KtmStyle.OK;

		case KO:
			return KtmStyle.KO;

		case SKIP:
			return KtmStyle.SKIP;
		default:
			throw new IllegalArgumentException("Unsupported DiffStatus: " + status);
		}
	}

	private void writeTotalCount(ExcelPosition excelPositionSheet, TSheet tSheet, ExcelIdCell idCell,
			CatalogStyles catalogStyles) throws BaseException {
		if (idCell != null) {
			ExcelPosition excelPositionCell = excelPositionSheet.clone();
			excelPositionCell.setRow(idCell.getRow());
			excelPositionCell.setColumn(idCell.getColumn());

			TCell tCell = excelAdapter.getOrCreateCell(tSheet, idCell.getRow(), idCell.getColumn());
			int value = readCellInteger(excelPositionCell, tCell);
			value++;
			excelAdapter.setValue(tCell, value);
			applyStyle(tCell, KtmStyle.NO, catalogStyles);
		}
	}

	private void writeStatusCount(ExcelPosition excelPositionSheet, TSheet tSheet, ExcelIdCell idCell,
			CatalogStyles catalogStyles, Status statusCell, Status statusResult) throws BaseException {
		if (idCell != null) {
			ExcelPosition excelPositionCell = excelPositionSheet.clone();
			excelPositionCell.setRow(idCell.getRow());
			excelPositionCell.setColumn(idCell.getColumn());

			TCell tCell = excelAdapter.getOrCreateCell(tSheet, idCell.getRow(), idCell.getColumn());
			int value = readCellInteger(excelPositionCell, tCell);
			if (statusCell == statusResult) {
				value++;
			}
			excelAdapter.setValue(tCell, value);
			if (value > 0) {
				KtmStyle ktmStyle = statusToStyle(statusCell);
				applyStyle(tCell, ktmStyle, catalogStyles);
			} else {
				applyStyle(tCell, KtmStyle.NO, catalogStyles);
			}
		}
	}

	private void writeStatusSheet(ExcelPosition excelPositionSheet, TSheet tSheet, StructureSheet structureSheet,
			Status status, CatalogStyles catalogStyles) throws BaseException {
		writeStatusCount(excelPositionSheet, tSheet, structureSheet.getIndexStatusSheetOk(), catalogStyles, Status.OK,
				status);
		writeStatusCount(excelPositionSheet, tSheet, structureSheet.getIndexStatusSheetKo(), catalogStyles, Status.KO,
				status);
		writeStatusCount(excelPositionSheet, tSheet, structureSheet.getIndexStatusSheetSkip(), catalogStyles,
				Status.SKIP, status);
	}

	private int readCellInteger(ExcelPosition excelPositionCell, TCell tCell) throws BaseException {
		String valueInteger = excelAdapter.getStringValue(tCell);
		int value = 0;
		if (!UtilsString.isEmpty(valueInteger)) {
			try {
				value = Integer.parseInt(valueInteger);
			} catch (Exception ex) {
				excelPositionCell.setRow(tCell.getRow());
				excelPositionCell.setColumn(tCell.getColumn());
				throw new ExcelException(excelPositionCell, "Value is not integer: " + valueInteger);
			}
		}
		return value;
	}

	private void updateExcelPosition(ExcelPosition excelPosition, int testIndex, int parameterIndex,
			Orientation orientation) {
		switch (orientation) {
		case V:
			excelPosition.setRow(parameterIndex);
			excelPosition.setColumn(testIndex);
			break;
		case H:
			excelPosition.setColumn(parameterIndex);
			excelPosition.setRow(testIndex);
			break;
		default:
			throw new IllegalArgumentException("Unsupported orientation: " + orientation);
		}
	}

	private TCell loadCell(TSheet tSheet, int testIndex, int parameterIndex, Orientation orientation) {
		switch (orientation) {
		case V:
			return excelAdapter.getOrCreateCell(tSheet, parameterIndex, testIndex);
		case H:
			return excelAdapter.getOrCreateCell(tSheet, testIndex, parameterIndex);
		default:
			throw new IllegalArgumentException("Unsupported orientation: " + orientation);
		}
	}

	private void workReview(ExcelPosition excelPositionWorkbook, TWorkbook tWorkbook, Test test, Status status,
			CatalogStyles catalogStyles) throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (!review) {
				LOG.trace("OK");
				return;
			}

			ExcelPosition excelPositionSheet = excelPositionWorkbook.clone();
			excelPositionSheet.setSheet(reviewSheet);

			TSheet tSheet = excelAdapter.getSheet(tWorkbook, reviewSheet);
			if (tSheet == null) {
				tSheet = excelAdapter.createSheet(tWorkbook, reviewSheet);
			}
			excelAdapter.setSheetOrder(tWorkbook, reviewSheet, 0);
			excelAdapter.setSheetActive(tWorkbook, reviewSheet);

			//
			// HEADER_COLUMN
			//
			if (reviewRowHeader != null) {
				TStyle tStyleHeader = catalogStyles.createStyle(KtmStyle.HEADER_COLUMN.name());

				TCell tCellHeaderName = excelAdapter.getOrCreateCell(tSheet, reviewRowHeader, reviewColumnName);
				excelAdapter.setValue(tCellHeaderName, "TYPE");
				excelAdapter.setCellStyle(tCellHeaderName, tStyleHeader);

				TCell tCellHeaderTotal = excelAdapter.getOrCreateCell(tSheet, reviewRowHeader, reviewColumnStatusTotal);
				excelAdapter.setValue(tCellHeaderTotal, "TOTAL");
				excelAdapter.setCellStyle(tCellHeaderTotal, tStyleHeader);

				TCell tCellHeaderOk = excelAdapter.getOrCreateCell(tSheet, reviewRowHeader, reviewColumnStatusOk);
				excelAdapter.setValue(tCellHeaderOk, "OK");
				excelAdapter.setCellStyle(tCellHeaderOk, tStyleHeader);

				TCell tCellHeaderKo = excelAdapter.getOrCreateCell(tSheet, reviewRowHeader, reviewColumnStatusKo);
				excelAdapter.setValue(tCellHeaderKo, "KO");
				excelAdapter.setCellStyle(tCellHeaderKo, tStyleHeader);

				TCell tCellHeaderSkip = excelAdapter.getOrCreateCell(tSheet, reviewRowHeader, reviewColumnStatusSkip);
				excelAdapter.setValue(tCellHeaderSkip, "SKIP");
				excelAdapter.setCellStyle(tCellHeaderSkip, tStyleHeader);
			}

			int rowMax = tSheet.getRowMax();

			//
			// Find position
			//
			Integer rowFound = null;
			Integer rowEmpty = null;
			boolean empty = true;
			int ir;
			for (ir = reviewRowFirst; ir <= rowMax; ir++) {
				TCell tCellName = excelAdapter.getOrCreateCell(tSheet, ir, reviewColumnName);
				String valueName = excelAdapter.getStringValue(tCellName);
				if (UtilsString.isEmpty(valueName)) {
					if (rowEmpty == null) {
						rowEmpty = ir;
					}
					continue;
				}
				if (test.getType().equals(valueName)) {
					rowFound = ir;
				}
				empty = false;
			}

			if (rowFound == null) {
				if (rowEmpty != null) {
					rowFound = rowEmpty;
				} else if (!empty) {
					rowFound = ir;
				} else {
					rowFound = reviewRowFirst;
				}
			}

			//
			// NAME
			//
			TStyle tStyleName = catalogStyles.createStyle(KtmStyle.HEADER_LINE.name());
			TCell tCellName = excelAdapter.getOrCreateCell(tSheet, rowFound, reviewColumnName);
			excelAdapter.setValue(tCellName, test.getType());
			excelAdapter.setCellStyle(tCellName, tStyleName);

			ExcelIdCell idCell = new ExcelIdCell();
			idCell.setRow(rowFound);

			idCell.setColumn(reviewColumnStatusTotal);
			writeTotalCount(excelPositionSheet, tSheet, idCell, catalogStyles);
			idCell.setColumn(reviewColumnStatusOk);
			writeStatusCount(excelPositionSheet, tSheet, idCell, catalogStyles, Status.OK, status);
			idCell.setColumn(reviewColumnStatusKo);
			writeStatusCount(excelPositionSheet, tSheet, idCell, catalogStyles, Status.KO, status);
			idCell.setColumn(reviewColumnStatusSkip);
			writeStatusCount(excelPositionSheet, tSheet, idCell, catalogStyles, Status.SKIP, status);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	//
	// STYLES
	//

	private void applyStyle(TCell tCell, KtmStyle ktmStyle, CatalogStyles catalogStyles) throws BaseException {
		TStyle cellStyle = excelAdapter.getCellStyle(tCell);
		TStyle newStyle = catalogStyles.updateStyle(ktmStyle.name(), cellStyle);
		// TStyle newStyle = catalogStyles.createStyle(ktmStyles.name());
		excelAdapter.setCellStyle(tCell, newStyle);
	}

}
