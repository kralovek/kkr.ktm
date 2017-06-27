package kkr.ktm.domains.excel.components.exceladapter.poi;

import org.apache.poi.ss.usermodel.Sheet;

import kkr.ktm.domains.excel.components.exceladapter.TSheet;

public class TSheetPoi implements TSheet {
	private String name;

	private TWorkbookPoi tWorkbookPoi;
	private Sheet sheet;

	private int rowMin;
	private int rowMax;
	private int columnMin;
	private int columnMax;

	public TSheetPoi(TWorkbookPoi tWorkbookPoi, Sheet sheet) {
		if (tWorkbookPoi == null) {
			throw new IllegalArgumentException("TWorkbookPoi is null");
		}
		if (sheet == null) {
			throw new IllegalArgumentException("Sheet is null");
		}
		this.tWorkbookPoi = tWorkbookPoi;
		this.sheet = sheet;
		this.name = sheet.getSheetName();
	}

	public String getName() {
		return name;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public int getRowMin() {
		return rowMin;
	}

	public void setRowMin(int rowMin) {
		this.rowMin = rowMin;
	}

	public int getRowMax() {
		return rowMax;
	}

	public void setRowMax(int rowMax) {
		this.rowMax = rowMax;
	}

	public int getColumnMin() {
		return columnMin;
	}

	public void setColumnMin(int columnMin) {
		this.columnMin = columnMin;
	}

	public int getColumnMax() {
		return columnMax;
	}

	public void setColumnMax(int columnMax) {
		this.columnMax = columnMax;
	}

	public TWorkbookPoi getPoiWorkbook() {
		return tWorkbookPoi;
	}

	public void setPoiWorkbook(TWorkbookPoi poiWorkbook) {
		this.tWorkbookPoi = poiWorkbook;
	}

	public String toString() {
		return name;
	}
}
