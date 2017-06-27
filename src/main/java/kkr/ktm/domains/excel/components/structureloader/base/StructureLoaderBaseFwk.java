package kkr.ktm.domains.excel.components.structureloader.base;

import java.util.Collection;
import java.util.HashSet;

import kkr.ktm.domains.common.components.selection.Selection;
import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.domains.excel.data.ExcelIdCell;
import kkr.ktm.domains.excel.utils.UtilsExcel;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.utils.excel.ExcelUtils;

public abstract class StructureLoaderBaseFwk {
	private boolean configured;

	protected ExcelAdapter excelAdapter;

	private Collection<Integer> _rowsIgnored;
	protected Collection<Integer> rowsIgnored;

	private Collection<String> _columnsIgnored;
	protected Collection<Integer> columnsIgnored;

	protected Selection selectionSheets;

	private String _cellStatusSheetOk;
	protected ExcelIdCell cellStatusSheetOk;

	private String _cellStatusSheetKo;
	protected ExcelIdCell cellStatusSheetKo;

	private String _cellStatusSheetSkip;
	protected ExcelIdCell cellStatusSheetSkip;

	protected int indexParameter;
	protected int indexIo;
	protected int indexCode;
	protected Integer indexActive;
	protected Integer indexName;
	protected Integer indexDescription;
	protected Integer indexGroup;
	protected Integer indexOrder;
	protected Integer indexStatusTest;

	public void config() throws ConfigurationException {
		configured = false;

		if (excelAdapter == null) {
			throw new ConfigurationException("Parameter 'excelAdapter' is not configured");
		}

		rowsIgnored = new HashSet<Integer>();
		if (_rowsIgnored != null) {
			int ir = 0;
			for (Integer _row : _rowsIgnored) {
				int row = ExcelUtils.getInstance().adaptAndCheckRowId(_row, "Parameter 'rowsIgnored[" + ir + "]'");
				rowsIgnored.add(row);
				ir++;
			}
		}

		columnsIgnored = new HashSet<Integer>();
		if (_columnsIgnored != null) {
			int ir = 0;
			for (String _column : _columnsIgnored) {
				int row = ExcelUtils.getInstance().adaptAndCheckColumnId(_column, "Parameter 'columnsIgnored[" + ir + "]'");
				columnsIgnored.add(row);
				ir++;
			}
		}

		if (selectionSheets == null) {
			throw new ConfigurationException("Parameter 'selectionSheets' is not configured");
		}

		cellStatusSheetOk = UtilsExcel.adaptAndCheckCellId(getClass(), _cellStatusSheetOk, "cellStatusSheetOk");
		cellStatusSheetKo = UtilsExcel.adaptAndCheckCellId(getClass(), _cellStatusSheetKo, "cellStatusSheetKo");
		cellStatusSheetSkip = UtilsExcel.adaptAndCheckCellId(getClass(), _cellStatusSheetSkip, "cellStatusSheetSkip");

		Collection<String> cells = new HashSet<String>();
		UtilsExcel.checkDoubles(cells, _cellStatusSheetOk, "cellStatusSheetOk");
		UtilsExcel.checkDoubles(cells, _cellStatusSheetKo, "cellStatusSheetKo");
		UtilsExcel.checkDoubles(cells, _cellStatusSheetSkip, "cellStatusSheetSkip");

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public ExcelAdapter getExcelAdapter() {
		return excelAdapter;
	}

	public void setExcelAdapter(ExcelAdapter excelAdapter) {
		this.excelAdapter = excelAdapter;
	}

	public Collection<Integer> getRowsIgnored() {
		return _rowsIgnored;
	}

	public void setRowsIgnored(Collection<Integer> rowsIgnored) {
		this._rowsIgnored = rowsIgnored;
	}

	public Collection<String> getColumnsIgnored() {
		return _columnsIgnored;
	}

	public void setColumnsIgnored(Collection<String> columnsIgnored) {
		this._columnsIgnored = columnsIgnored;
	}

	public String getCellStatusSheetOk() {
		return _cellStatusSheetOk;
	}

	public void setCellStatusSheetOk(String cellStatusSheetOk) {
		this._cellStatusSheetOk = cellStatusSheetOk;
	}

	public String getCellStatusSheetKo() {
		return _cellStatusSheetKo;
	}

	public void setCellStatusSheetKo(String cellStatusSheetKo) {
		this._cellStatusSheetKo = cellStatusSheetKo;
	}

	public String getCellStatusSheetSkip() {
		return _cellStatusSheetSkip;
	}

	public void setCellStatusSheetSkip(String cellStatusSheetSkip) {
		this._cellStatusSheetSkip = cellStatusSheetSkip;
	}

	public Selection getSelectionSheets() {
		return selectionSheets;
	}

	public void setSelectionSheets(Selection selectionSheets) {
		this.selectionSheets = selectionSheets;
	}
}
