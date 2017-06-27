package kkr.ktm.domains.excel.components.exceladapter;

import java.io.File;

import kkr.ktm.exception.BaseException;

public interface ExcelAdapter {

	TWorkbook cloneWorkbook(TWorkbook tWorkbookSource, File file) throws BaseException;

	TWorkbook createWorkbook(File file) throws BaseException;

	void closeWorkbook(TWorkbook tWorkbook) throws BaseException;

	void saveWorkbook(TWorkbook tWorkbook) throws BaseException;

	TWorkbook readWorkbook(File file) throws BaseException;

	void setSheetOrder(TWorkbook tWorkbook, String sheet, int order);

	void setSheetActive(TWorkbook tWorkbook, String sheet);

	TSheet getSheet(TWorkbook tWorkbook, String name);

	TSheet createSheet(TWorkbook tWorkbook, String name);

	TSheet getSheet(TWorkbook tWorkbook, int index);

	void removeSheet(TWorkbook tWorkbook, String name);

	int getSheetCount(TWorkbook tWorkbook);

	TCell getCell(TSheet tSheet, int row, int column);

	TCell getOrCreateCell(TSheet tSheet, int row, int column);

	TCell createCell(TSheet tSheet, int irow, int icolumn);

	Object getValue(TCell tCell) throws BaseException;

	String getStringValue(TCell tCell) throws BaseException;

	void setValue(TCell tCell, TCell tCellTemplate, Object value);

	void setValue(TCell tCell, Object value);

	TStyle getCellStyle(TCell tCell);

	void setCellStyle(TCell tCell, TStyle tStyle);

	void copyCellStyle(TCell tCellSource, TCell tCelltarget);
}
