package kkr.ktm.domains.excel.components.exceladapter.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TStyle;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsFile;

public class ExcelAdapterPoi extends ExcelAdapterPoiFwk implements ExcelAdapter {
	private static final Logger LOG = Logger.getLogger(ExcelAdapterPoi.class);

	private static final long MS_1900_1970 = 2209078800000L;

	public TWorkbook cloneWorkbook(TWorkbook tWorkbookSource, File file) throws BaseException {
		LOG.trace("BEGIN: " + file.getAbsolutePath());
		try {
			testConfigured();

			TWorkbookPoi tWorkbookPoiSource = cast(tWorkbookSource);
			TWorkbookPoi tWorkbookPoiTarget = new TWorkbookPoi(tWorkbookPoiSource.getWorkbook(), file);
			saveWorkbook(tWorkbookPoiTarget);
			closeWorkbook(tWorkbookPoiTarget);

			tWorkbookPoiTarget = readWorkbook(file);

			setDateTimeFormat(tWorkbookPoiTarget);

			LOG.trace("OK");
			return tWorkbookPoiTarget;
		} finally {
			LOG.trace("END");
		}
	}

	public TWorkbook createWorkbook(File file) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			if (file == null) {
				throw new IllegalArgumentException("The parameter file may not be null");
			}

			Workbook workbook = null;
			if (file.getName().toLowerCase().endsWith(".xlsx")) {
				workbook = new XSSFWorkbook();
			} else if (file.getName().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook();
			} else {
				throw new TechnicalException("The extension of the file must be .xlsx or .xls");
			}

			TWorkbookPoi tWorkbookPoi = new TWorkbookPoi(workbook, file);

			setDateTimeFormat(tWorkbookPoi);

			LOG.trace("OK");
			return tWorkbookPoi;
		} finally {
			LOG.trace("END");
		}
	}

	private void setDateTimeFormat(TWorkbookPoi tWorkbookPoi) {
		short fDate = tWorkbookPoi.getWorkbook().getCreationHelper().createDataFormat().getFormat(formatDate.toPattern());
		short fTime = tWorkbookPoi.getWorkbook().getCreationHelper().createDataFormat().getFormat(formatTime.toPattern());
		short fInteger = tWorkbookPoi.getWorkbook().getCreationHelper().createDataFormat().getFormat("0");
		short fDouble = tWorkbookPoi.getWorkbook().getCreationHelper().createDataFormat().getFormat("0.00");

		tWorkbookPoi.setFormatDate(fDate);
		tWorkbookPoi.setFormatTime(fTime);
		tWorkbookPoi.setFormatInteger(fInteger);
		tWorkbookPoi.setFormatDouble(fDouble);
	}

	public void saveWorkbook(TWorkbook tWorkbook) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
			Workbook workbook = tWorkbookPoi.getWorkbook();

			createFileDirectory(tWorkbookPoi.getFile());
			LOG.info("saving: " + tWorkbookPoi.getFile());

			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(tWorkbookPoi.getFile());
				workbook.write(outputStream);
				outputStream.close();
				outputStream = null;
			} catch (IOException ex) {
				throw new TechnicalException("Cannot write the file: " + tWorkbookPoi.getFile().getAbsolutePath(), ex);
			} finally {
				UtilsFile.getInstance().close(outputStream);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public TWorkbookPoi readWorkbook(File file) throws BaseException {
		LOG.trace("BEGIN: " + file.getAbsolutePath());
		try {
			testConfigured();

			TWorkbookPoi tWorkbookPoi = null;
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				Workbook workbook = WorkbookFactory.create(fileInputStream);

				tWorkbookPoi = new TWorkbookPoi(workbook, file);
				tWorkbookPoi.setInputStream(fileInputStream);

				LOG.trace("OK");
				return tWorkbookPoi;
			} catch (FileNotFoundException ex) {
				throw new TechnicalException("The excel file does not exist: " + file.getAbsolutePath(), ex);
			} catch (IOException ex) {
				throw new TechnicalException("Cannot read the excel file: " + file.getAbsolutePath(), ex);
			} catch (InvalidFormatException ex) {
				throw new TechnicalException("Bad format of the excel file: " + file.getAbsolutePath(), ex);
			} finally {
				if (tWorkbookPoi == null) {
					UtilsFile.getInstance().close(fileInputStream);
				}
			}

		} finally {
			LOG.trace("END");
		}
	}

	public void closeWorkbook(TWorkbook tWorkbook) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
			InputStream inputStream = tWorkbookPoi.getInputStream();
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			} catch (IOException ex) {
				throw new TechnicalException("Cannot close the workbook: " + tWorkbookPoi.getFile().getAbsolutePath(), ex);
			} finally {
				UtilsFile.getInstance().close(inputStream);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public TSheetPoi getSheet(TWorkbook tWorkbook, String name) {
		testConfigured();
		try {
			TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
			Workbook workbook = tWorkbookPoi.getWorkbook();
			Sheet sheet = workbook.getSheet(name);
			if (sheet == null) {
				return null;
			}
			TSheetPoi retval = new TSheetPoi(tWorkbookPoi, sheet);
			evaluateSheetRange(retval);
			return retval;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Incorrect tWorkbook parameter", ex);
		}
	}

	private TWorkbookPoi cast(TWorkbook tWorkbook) {
		if (tWorkbook == null) {
			throw new IllegalArgumentException("TWorkbook is null");
		}
		if (!(tWorkbook instanceof TWorkbookPoi)) {
			throw new IllegalArgumentException("TWorkbook is not TWokrbookPoi: " + tWorkbook.getClass().getName());
		}
		return (TWorkbookPoi) tWorkbook;
	}

	private TSheetPoi cast(TSheet tSheet) {
		if (tSheet == null) {
			throw new IllegalArgumentException("TSheet is null");
		}
		if (!(tSheet instanceof TSheetPoi)) {
			throw new IllegalArgumentException("TSheet is not TSheetPoi: " + tSheet.getClass().getName());
		}
		return (TSheetPoi) tSheet;
	}

	private TCellPoi cast(TCell tCell) {
		if (tCell == null) {
			throw new IllegalArgumentException("TCell is null");
		}
		if (!(tCell instanceof TCellPoi)) {
			throw new IllegalArgumentException("TCell is not TCellPoi: " + tCell.getClass().getName());
		}
		return (TCellPoi) tCell;
	}

	public void setSheetOrder(TWorkbook tWorkbook, String sheet, int order) {
		testConfigured();
		TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
		Workbook workbook = tWorkbookPoi.getWorkbook();

		Sheet sheetPoi = workbook.getSheet(sheet);
		if (sheetPoi == null) {
			throw new IllegalArgumentException("The sheet does not exist in the workbook: " + sheet);
		}

		workbook.setSheetOrder(sheet, order);
	}

	public void setSheetActive(TWorkbook tWorkbook, String sheet) {
		testConfigured();
		TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
		Workbook workbook = tWorkbookPoi.getWorkbook();

		Sheet sheetPoi = workbook.getSheet(sheet);
		if (sheetPoi == null) {
			throw new IllegalArgumentException("The sheet does not exist in the workbook: " + sheet);
		}

		int iSheet = workbook.getSheetIndex(sheetPoi);

		workbook.setActiveSheet(iSheet);
	}

	public TSheetPoi getSheet(TWorkbook tWorkbook, int index) {
		testConfigured();
		TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
		Workbook workbook = tWorkbookPoi.getWorkbook();

		Sheet sheet = workbook.getSheetAt(index);
		if (sheet == null) {
			return null;
		}
		TSheetPoi retval = new TSheetPoi(tWorkbookPoi, sheet);
		evaluateSheetRange(retval);
		return retval;
	}

	public int getSheetCount(TWorkbook tWorkbook) {
		testConfigured();
		TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
		Workbook workbook = tWorkbookPoi.getWorkbook();
		return workbook.getNumberOfSheets();
	}

	public void removeSheet(TWorkbook tWorkbook, String name) {
		testConfigured();
		TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
		Workbook workbook = tWorkbookPoi.getWorkbook();

		Sheet sheet = workbook.getSheet(name);
		if (sheet != null) {
			int sheetIndex = workbook.getSheetIndex(sheet);
			workbook.removeSheetAt(sheetIndex);
		}
	}

	public TSheet createSheet(TWorkbook tWorkbook, String name) {
		testConfigured();
		TWorkbookPoi tWorkbookPoi = cast(tWorkbook);
		Workbook workbook = tWorkbookPoi.getWorkbook();

		TSheet tSheet = getSheet(tWorkbookPoi, name);
		if (tSheet != null) {
			return tSheet;
		}

		Sheet sheet = workbook.createSheet(name);

		TSheetPoi tSheetPoi = new TSheetPoi(tWorkbookPoi, sheet);
		return tSheetPoi;
	}

	public TCellPoi createCell(TSheet tSheet, int irow, int icolumn) {
		testConfigured();
		TSheetPoi tSheetPoi = cast(tSheet);
		Sheet sheet = tSheetPoi.getSheet();

		Row row = sheet.getRow(irow);
		if (row == null) {
			row = sheet.createRow(irow);
		}

		Cell cell = row.getCell(icolumn, Row.CREATE_NULL_AS_BLANK);

		TCellPoi tCellPoi = new TCellPoi(tSheetPoi, cell);
		tCellPoi.setColumn(icolumn);
		tCellPoi.setRow(irow);

		return tCellPoi;
	}

	public TCellPoi getCell(TSheet tSheet, int irow, int icolumn) {
		testConfigured();

		TSheetPoi tSheetPoi = cast(tSheet);
		Sheet sheet = tSheetPoi.getSheet();

		Cell cell = null;
		Row row = sheet.getRow(irow);
		if (row == null) {
			return null;
		}

		cell = row.getCell(icolumn, Row.RETURN_BLANK_AS_NULL);
		if (cell == null) {
			return null;
		}

		TCellPoi tCellPoi = new TCellPoi(tSheetPoi, cell);
		tCellPoi.setColumn(icolumn);
		tCellPoi.setRow(irow);

		return tCellPoi;
	}

	public TCellPoi getOrCreateCell(TSheet tSheet, int irow, int icolumn) {
		testConfigured();

		TSheetPoi tSheetPoi = cast(tSheet);
		Sheet sheet = tSheetPoi.getSheet();

		Cell cell = null;
		Row row = sheet.getRow(irow);
		if (row != null) {
			cell = row.getCell(icolumn, Row.CREATE_NULL_AS_BLANK);
		}

		TCellPoi tCellPoi = new TCellPoi(tSheetPoi, cell);
		tCellPoi.setColumn(icolumn);
		tCellPoi.setRow(irow);

		return tCellPoi;
	}

	public Object getValue(TCell tCell) throws BaseException {
		testConfigured();
		if (tCell == null) {
			return null;
		}
		TCellPoi tCellPoi = cast(tCell);
		Cell cell = tCellPoi.getCell();

		if (cell == null) {
			return null;
		}

		FormulaEvaluator evaluator = tCellPoi.getPoiSheet().getPoiWorkbook().getEvaluator();
		CellValue cellValue = null;
		try {
			cellValue = evaluator.evaluate(cell);
		} catch (Exception ex) {
			String message = "Cannot evaluate the cell value: " + cell.toString();
			if (evaluator instanceof FormulaEvaluator) {
				message += " Check for empty arguments in the expression.";
			}
			throw new TechnicalException(message, ex);
		}

		if (cellValue == null) {
			return null;
		}

		switch (cellValue.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN :
				return cellValue.getBooleanValue();
			case Cell.CELL_TYPE_NUMERIC : {
				Date date = toDate(cell, cellValue);
				if (date == null) {
					Double valueDouble = cellValue.getNumberValue();
					long valueLong = valueDouble.longValue();
					Double valueLongDouble = new Double(valueLong);
					if (valueDouble.equals(valueLongDouble)) {
						return valueLong;
					} else {
						return valueDouble;
					}
				} else {
					return date;
				}
			}
			case Cell.CELL_TYPE_STRING :
			default :
				return cellValue.getStringValue();
		}
	}

	private Date toDate(Cell cell, CellValue cellValue) {
		if (cellValue == null) {
			return null;
		}
		CellStyle cellStyle = cell.getCellStyle();
		String dataFormatString = cellStyle.getDataFormatString();

		if (dataFormatString == null || dataFormatString.isEmpty()) {
			return null;
		}

		if (dataFormatString.matches(".*[dMyhmsS].*")) {
			double value = cellValue.getNumberValue();
			long ms = (long) (value * 24 * 60 * 60 * 1000);
			Date date = new Date(ms - MS_1900_1970);
			return date;
		} else {
			return null;
		}
	}

	public String getStringValue(TCell tCell) throws BaseException {
		testConfigured();
		Object object = getValue(tCell);
		if (object == null) {
			return null;
		}

		return String.valueOf(object);
	}

	public void setCellStyle(TCell tCell, TStyle tStyle) {
		TCellPoi tCellPoi = cast(tCell);
		Cell cell = getCellFromPoi(tCellPoi, tCell.getRow(), tCell.getColumn());

		TStylePoi tStylePoi = (TStylePoi) tStyle;
		CellStyle cellStyle = tStylePoi.getCellStyle();

		CellStyle cellStyleOrg = cell.getCellStyle();
		cellStyleOrg.getDataFormat();

		cell.setCellStyle(cellStyle);
		cellStyle.setDataFormat(cellStyleOrg.getDataFormat());
	}

	private Cell getCellFromPoi(TCellPoi tCellPoi, int idRow, int idColumn) {
		Cell cell = tCellPoi.getCell();
		if (cell == null) {
			TSheetPoi tSheetPoi = tCellPoi.getPoiSheet();
			Sheet sheet = tSheetPoi.getSheet();
			Row row = sheet.getRow(idRow);
			if (row == null) {
				row = sheet.createRow(idRow);
			}
			cell = row.getCell(idColumn);
			if (cell == null) {
				cell = row.createCell(idColumn, Cell.CELL_TYPE_BLANK);
			}
		}
		return cell;
	}

	public void copyCellStyle(TCell tCellSource, TCell tCellTarget) {
		if (tCellTarget != null && tCellSource != null) {
			TCellPoi tCellPoiSource = (TCellPoi) tCellSource;
			Cell cellSource = tCellPoiSource.getCell();
			TCellPoi tCellPoiTarget = (TCellPoi) tCellTarget;
			Cell cellTarget = tCellPoiTarget.getCell();
			cellTarget.setCellStyle(cellSource.getCellStyle());
		}
	}

	public void setValue(TCell tCell, TCell tCellTemplate, Object value) {
		setValue(tCell, value);
		if (tCellTemplate != null) {
			TCellPoi tCellPoiTemplate = (TCellPoi) tCellTemplate;
			Cell cellTemplate = tCellPoiTemplate.getCell();

			TCellPoi tCellPoi = (TCellPoi) tCell;
			Cell cell = tCellPoi.getCell();

			if (cell != null && cellTemplate != null) {
				CellStyle cellStyle = cell.getCellStyle();
				CellStyle cellStyleTemplate = cellTemplate.getCellStyle();
				cellStyle.setDataFormat(cellStyleTemplate.getDataFormat());
			}
		}
	}

	public void setValue(TCell tCell, Object value) {
		TCellPoi tCellPoi = (TCellPoi) tCell;
		Cell cell = tCellPoi.getCell();

		TSheetPoi tSheetPoi = tCellPoi.getPoiSheet();

		TWorkbookPoi tWorkbookPoi = tSheetPoi.getPoiWorkbook();
		Workbook workbook = tWorkbookPoi.getWorkbook();

		if (value == null) {
			cell.setCellValue((String) null);
		} else {
			if (value instanceof Date) {
				Date valueDate = (Date) value;
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(valueDate);

				CellStyle cellStyleNew = workbook.createCellStyle();
				cellStyleNew.setDataFormat(tWorkbookPoi.getFormatDate());
				cell.setCellStyle(cellStyleNew);
			} else if (value instanceof Number) {
				Number valueNumber = (Number) value;
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(valueNumber.doubleValue());

				CellStyle cellStyleNew = workbook.createCellStyle();
				if (value instanceof Long || value instanceof Integer || value instanceof Short) {
					cellStyleNew.setDataFormat(tWorkbookPoi.getFormatInteger());
				} else if (value instanceof Double || value instanceof Float) {
					cellStyleNew.setDataFormat(tWorkbookPoi.getFormatDouble());
				}
				cell.setCellStyle(cellStyleNew);
			} else if (value instanceof Boolean) {
				Boolean valueBoolean = (Boolean) value;
				cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
				cell.setCellValue(valueBoolean);
			} else {
				String valueString = (String) value;
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(valueString);
			}
		}
	}

	private void createFileDirectory(File file) throws TechnicalException {
		if (file != null) {
			File dir = file.getParentFile();
			if (dir != null && !dir.isDirectory() && !dir.mkdirs()) {
				throw new TechnicalException("Cannot create the directory: " + dir.getAbsolutePath());
			}
		}
	}

	private void evaluateSheetRange(TSheetPoi tSheetPoi) {
		Sheet sheet = tSheetPoi.getSheet();
		if (sheet == null) {
			tSheetPoi.setRowMin(0);
			tSheetPoi.setRowMax(0);
			tSheetPoi.setColumnMin(0);
			tSheetPoi.setColumnMax(0);
		} else {
			tSheetPoi.setRowMin(sheet.getFirstRowNum());
			tSheetPoi.setRowMax(sheet.getLastRowNum());
			Short columnMin = null;
			Short columnMax = null;
			for (int ir = sheet.getFirstRowNum(); ir <= sheet.getLastRowNum(); ir++) {
				Row row = sheet.getRow(ir);
				if (row == null) {
					continue;
				}
				if (columnMin == null) {
					columnMin = row.getFirstCellNum();
				}
				if (columnMax == null) {
					columnMax = row.getLastCellNum();
				}
				if (row.getFirstCellNum() < columnMin) {
					columnMin = row.getFirstCellNum();
				}
				if (row.getLastCellNum() > columnMax) {
					columnMax = row.getLastCellNum();
				}
			}
			if (columnMin == null) {
				columnMin = 0;
			}
			if (columnMax == null) {
				columnMax = 0;
			}
			tSheetPoi.setColumnMin(columnMin);
			tSheetPoi.setColumnMax(columnMax);
		}
	}

	public TStyle getCellStyle(TCell tCell) {
		TCellPoi tCellPoi = (TCellPoi) tCell;
		Cell cell = tCellPoi.getCell();
		CellStyle cellStyle = cell.getCellStyle();
		TStyle tStyle = new TStylePoi(cellStyle);
		return tStyle;
	}
}
