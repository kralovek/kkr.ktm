package kkr.ktm.domains.tests.components.testloader.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.domains.excel.data.Io;
import kkr.ktm.domains.excel.data.Orientation;
import kkr.ktm.domains.excel.data.StructureParameter;
import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureTest;
import kkr.ktm.domains.excel.data.StructureWorkbook;
import kkr.ktm.domains.tests.components.testloader.TestLoader;
import kkr.ktm.domains.tests.data.TestInput;
import kkr.common.errors.BaseException;
import kkr.common.errors.ExcelException;
import kkr.common.errors.TechnicalException;
import kkr.common.utils.excel.ExcelPosition;

public class TestLoaderExcel extends TestLoaderExcelFwk implements TestLoader {
	private static final Logger LOG = Logger.getLogger(TestLoaderExcel.class);

	public Collection<TestInput> loadTests(String source) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File file = new File(source);
			TWorkbook workbook = null;
			try {
				workbook = excelAdapter.readWorkbook(file);
				ExcelPosition excelPosition = new ExcelPosition();
				excelPosition.setFile(file);

				Collection<TestInput> testsWorkbook = workWorkbook(excelPosition, workbook, source);

				LOG.trace("OK");
				return testsWorkbook;
			} finally {

			}
		} finally {
			LOG.trace("END");
		}
	}

	private Collection<TestInput> workWorkbook(ExcelPosition excelPosition, TWorkbook tWorkbook, String source) throws BaseException {
		int sheetCount = excelAdapter.getSheetCount(tWorkbook);

		Collection<TestInput> testsWorkbook = new TreeSet<TestInput>();

		StructureWorkbook structureWorkbook = structureLoader.loadStructureWorkbook(excelPosition, tWorkbook);

		for (int i = 0; i < sheetCount; i++) {
			TSheet tSheet = excelAdapter.getSheet(tWorkbook, i);

			StructureSheet structureSheet = structureWorkbook.getSheets().get(tSheet.getName());

			if (structureSheet == null) {
				LOG.info("Sheet ignored: " + tSheet.getName());
				continue;
			}

			Collection<TestInput> testsSheet = workSheet(excelPosition, tSheet, structureSheet, i, source);
			if (testsSheet != null && !testsSheet.isEmpty()) {
				testsWorkbook.addAll(testsSheet);
			}
		}

		checkDoubles(excelPosition, testsWorkbook);

		return testsWorkbook;
	}

	private void checkDoubles(ExcelPosition excelPosition, Collection<TestInput> testInputs) throws BaseException {
		Map<String, TestInput> codeTestInputs = new HashMap<String, TestInput>();
		Map<String, Collection<TestInput>> codeDoubles = new HashMap<String, Collection<TestInput>>();

		for (TestInput testInput : testInputs) {
			if (codeTestInputs.containsKey(testInput.getCode())) {
				Collection<TestInput> doubles = codeDoubles.get(testInput.getCode());
				if (doubles == null) {
					doubles = new ArrayList<TestInput>();
					doubles.add(codeTestInputs.get(testInput.getCode()));
					codeDoubles.put(testInput.getCode(), doubles);
				}
				doubles.add(testInput);
			}
		}
		if (!codeDoubles.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			for (Map.Entry<String, Collection<TestInput>> entry : codeDoubles.entrySet()) {
				if (buffer.length() != 0) {
					buffer.append(",");
				}
				buffer.append(entry.getKey());
			}
			throw new ExcelException(excelPosition, "Some test-codes are not unique in the workbook: " + buffer.toString());
		}
	}

	private Object readValue(ExcelPosition excelPosition, TSheet tSheet, int indexTest, int indexParameter, Orientation orientation, Io io)
			throws BaseException {
		TCell tCell;
		switch (orientation) {
			case V :
				excelPosition.setRow(indexParameter);
				excelPosition.setColumn(indexTest);
				tCell = excelAdapter.getCell(tSheet, indexParameter, indexTest);
				break;
			case H :
				excelPosition.setColumn(indexParameter);
				excelPosition.setRow(indexTest);
				tCell = excelAdapter.getCell(tSheet, indexTest, indexParameter);
				break;

			default :
				throw new TechnicalException("Unsupported rientation: " + orientation);
		}

		Object value = excelAdapter.getValue(tCell);

		Object retval;
		switch (io) {
			case I :
				retval = valueGenerator.parseValue(excelPosition, value);
				break;

			case E :
				retval = valueGenerator.parsePattern(excelPosition, value);
				break;
			default :
				throw new IllegalArgumentException("Unexpected IO: " + io);
		}
		return retval;
	}

	private Collection<TestInput> workSheet(ExcelPosition excelPositionWorkbook, TSheet tSheet, StructureSheet structureSheet, int orderOfSheet,
			String source) throws BaseException {
		Collection<TestInput> testsSheet = new ArrayList<TestInput>();

		LOG.debug("SHEET: " + tSheet.getName());

		ExcelPosition excelPositionSheet = excelPositionWorkbook.clone();
		excelPositionSheet.setSheet(tSheet.getName());

		ExcelPosition excelPosition = excelPositionSheet.clone();

		Iterator<StructureTest> iteratorT = structureSheet.iteratorTests();
		while (iteratorT.hasNext()) {
			StructureTest structureTest = iteratorT.next();

			TestInputExcel testInputExcel = new TestInputExcel();
			testInputExcel.setSource(source);
			testInputExcel.setType(tSheet.getName());
			testInputExcel.setCode(structureTest.getCode());
			testInputExcel.setName(structureTest.getName());
			testInputExcel.setDescription(structureTest.getDescription());

			testInputExcel.setOrderOfSheet(orderOfSheet);
			testInputExcel.setOrderInSheet(structureTest.getIndex());
			testInputExcel.setOrder(structureTest.getOrder());
			testInputExcel.setGroup(structureTest.getGroup());

			Iterator<StructureParameter> iteratorI = structureSheet.iteratorParametersI();
			while (iteratorI.hasNext()) {
				StructureParameter structureParameterI = iteratorI.next();
				Object value = readValue(excelPosition, tSheet, structureTest.getIndex(), structureParameterI.getIndex(),
						structureSheet.getOrientation(), Io.I);
				testInputExcel.getDataInput().put(structureParameterI.getName(), value);
			}

			Iterator<StructureParameter> iteratorE = structureSheet.iteratorParametersE();
			while (iteratorE.hasNext()) {
				StructureParameter structureParameterE = iteratorE.next();
				// Read and forget - just for checking
				readValue(excelPosition, tSheet, structureTest.getIndex(), structureParameterE.getIndex(), structureSheet.getOrientation(), Io.E);
			}

			testsSheet.add(testInputExcel);
		}

		return testsSheet;
	}
}
