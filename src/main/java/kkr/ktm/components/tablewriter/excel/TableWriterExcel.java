package kkr.ktm.components.tablewriter.excel;

import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.ktm.components.tablewriter.TableWriter;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;

public class TableWriterExcel extends TableWriterExcelFwk implements TableWriter {
	private static final Logger LOG = Logger.getLogger(TableWriterExcel.class);

	public void writeData(String target, Collection<String> parameters, Collection<Collection<Object>> data) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File file = new File(dir, target + ".xlsx");

			if (file.exists() && !file.delete()) {
				throw new TechnicalException("Cannot remove the file: " + file.getAbsolutePath());
			}

			TWorkbook tWorkbook = excelAdapter.createWorkbook(file);

			workWorkbook(tWorkbook, target, parameters, data);

			excelAdapter.saveWorkbook(tWorkbook);

			excelAdapter.closeWorkbook(tWorkbook);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void workWorkbook(TWorkbook tWorkbook, String target, Collection<String> parameters, Collection<Collection<Object>> data)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			excelAdapter.removeSheet(tWorkbook, target);
			TSheet tSheet = excelAdapter.createSheet(tWorkbook, target);

			int iColumn = 0;
			int iRow = 0;

			if (parameters != null) {
				for (String parameter : parameters) {
					TCell tCell = excelAdapter.getOrCreateCell(tSheet, iRow, iColumn);
					excelAdapter.setValue(tCell, parameter);
					iColumn++;
				}
				iRow++;
			}

			if (data != null) {
				for (Collection<Object> line : data) {
					if (line == null) {
						throw new IllegalArgumentException("Line " + iRow + " is NULL");
					}
					if (line.size() != parameters.size()) {
						throw new IllegalArgumentException(
								"Line " + iRow + " has " + line.size() + " values, but expected is " + parameters.size() + " values");
					}
					iColumn = 0;
					for (Object value : line) {
						TCell tCell = excelAdapter.getOrCreateCell(tSheet, iRow, iColumn);
						excelAdapter.setValue(tCell, value);
						iColumn++;
					}
					iRow++;
				}
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
