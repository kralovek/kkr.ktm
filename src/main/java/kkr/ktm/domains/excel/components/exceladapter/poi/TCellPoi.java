package kkr.ktm.domains.excel.components.exceladapter.poi;

import org.apache.poi.ss.usermodel.Cell;

import kkr.ktm.domains.excel.components.exceladapter.TCell;

public class TCellPoi implements TCell {

	private int row;
	private int column;

	private TSheetPoi poiSheet;

	private Cell cell;

	public TCellPoi(TSheetPoi tSheetPoi, Cell cell) {
		if (tSheetPoi == null) {
			throw new IllegalArgumentException("TSheetPoi is null");
		}
		if (cell == null) {
			throw new IllegalArgumentException("Cell is null");
		}
		this.poiSheet = tSheetPoi;
		this.cell = cell;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		if (cell == null) {
			throw new IllegalArgumentException("Cell is null");
		}
		this.cell = cell;
	}

	public TSheetPoi getPoiSheet() {
		return poiSheet;
	}

	public String toString() {
		return "[" + row + ":" + column + "]";
	}
}
