package kkr.ktm.components.tablereader.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.components.tablereader.TableReader;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.exception.BaseException;
import kkr.ktm.utils.excel.ExcelConfigurationException;
import kkr.ktm.utils.excel.ExcelPosition;

public class TableReaderExcel extends TableReaderExcelFwk implements TableReader {
	private static final Logger LOG = Logger.getLogger(TableReaderExcel.class);

	public Collection<Map<String, Object>> readData(String source) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File file = generateFile(source);

			TWorkbook tWorkbook = null;

			tWorkbook = excelAdapter.readWorkbook(file);

			ExcelPosition excelPosition = new ExcelPosition();
			excelPosition.setFile(file);

			TSheet tSheet;
			if (sheet != null) {
				tSheet = excelAdapter.getSheet(tWorkbook, sheet);
				if (tSheet == null) {
					throw new ExcelConfigurationException(excelPosition, "Excel file does not contain the sheet: " + sheet);
				}
			} else {
				tSheet = excelAdapter.getSheet(tWorkbook, 0);
				if (tSheet == null) {
					throw new ExcelConfigurationException(excelPosition, "Excel file does not contain any");
				}
			}

			excelPosition.setSheet(tSheet.getName());

			Collection<Map<String, Object>> retval = workSheet(excelPosition, tSheet);

			excelAdapter.closeWorkbook(tWorkbook);
			tWorkbook = null;

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Collection<Map<String, Object>> workSheet(ExcelPosition excelPositionSheet, TSheet tSheet) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<Map<String, Object>> dataSheet = new ArrayList<Map<String, Object>>();
			List<String> header = workHeader(excelPositionSheet, tSheet);

			Integer irEmpty = null;
			int nRows = tSheet.getRowMax();

			ExcelPosition excelPositionRow = excelPositionSheet.clone();

			for (int ir = 1; ir <= nRows; ir++) {
				excelPositionRow.setRow(ir);

				Map<String, Object> dataRow = new LinkedHashMap<String, Object>();
				int ic = 0;
				boolean rEmpty = true;
				for (String headerColumn : header) {
					TCell tCell = excelAdapter.getOrCreateCell(tSheet, ir, ic);
					Object value = excelAdapter.getValue(tCell);
					if (!isEmpty(value)) {
						rEmpty = false;
						dataRow.put(headerColumn, value);
					} else {
						dataRow.put(headerColumn, null);
					}
					ic++;
				}

				if (rEmpty) {
					if (irEmpty == null) {
						irEmpty = ir;
					}
					continue;
				}

				if (irEmpty != null) {
					throw new ExcelConfigurationException(excelPositionRow, "There is an emmpty row in the data block");
				}

				dataSheet.add(dataRow);
			}

			LOG.trace("OK");
			return dataSheet;
		} finally {
			LOG.trace("END");
		}

	}

	private boolean isEmpty(Object value) {
		return value == null || value instanceof String && ((String) value).isEmpty();
	}

	private List<String> workHeader(ExcelPosition excelPositionSheet, TSheet tSheet) throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<String> retval = new ArrayList<String>();

			ExcelPosition excelPositionRow = excelPositionSheet.clone();
			excelPositionRow.setRow(0);

			int nColumns = tSheet.getColumnMax();

			Integer icEmpty = null;
			for (int ic = 0; ic <= nColumns; ic++) {
				TCell tCell = excelAdapter.getOrCreateCell(tSheet, 0, ic);
				Object value = excelAdapter.getValue(tCell);

				if (isEmpty(value)) {
					if (icEmpty == null) {
						icEmpty = ic;
					}
					continue;
				}

				if (icEmpty != null) {
					excelPositionRow.setRow(icEmpty);
					throw new ExcelConfigurationException(excelPositionRow, "There is an emmpty cell in the header row");
				}

				String valueString = value.toString();
				if (retval.contains(valueString)) {
					excelPositionRow.setRow(ic);
					throw new ExcelConfigurationException(excelPositionRow, "Duplicity in the header: " + valueString);
				}

				retval.add(valueString);
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private File generateFile(String source) {
		File file = new File(dir, source + ".xlsx");
		return file;
	}
}
