package kkr.ktm.domains.excel.components.structureloader.vertical;

import java.util.HashSet;
import java.util.Set;

import kkr.ktm.domains.excel.components.structureloader.base.StructureLoaderBase;
import kkr.ktm.domains.excel.utils.UtilsExcel;
import kkr.ktm.exception.ConfigurationException;

public abstract class StructureLoaderVerticalFwk extends StructureLoaderBase {
	private boolean configured;

	private String _columnParameter;
	private String _columnIo;
	private Integer _rowCode;
	private Integer _rowActive;
	private Integer _rowName;
	private Integer _rowDescription;
	private Integer _rowGroup;
	private Integer _rowOrder;
	private Integer _rowStatusTest;

	public void config() throws ConfigurationException {
		configured = false;
		super.config();

		if (_columnParameter == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'columnParameter' is not configured");
		} else {
			indexParameter = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnParameter, "Parameter 'columnParameter'");
		}
		if (_columnIo == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'columnIo' is not configured");
		} else {
			indexIo = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnIo, "Parameter 'columnIo'");
		}
		if (_rowCode == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'rowCode' is not configured");
		} else {
			indexCode = UtilsExcel.adaptAndCheckRowId(getClass(), _rowCode, "Parameter 'rowCode'");
		}
		if (_rowActive == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'rowActive' is not configured");
		} else {
			indexActive = UtilsExcel.adaptAndCheckRowId(getClass(), _rowActive, "Parameter 'rowActive'");
		}
		if (_rowStatusTest == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'rowStatusTest' is not configured");
		} else {
			indexStatusTest = UtilsExcel.adaptAndCheckRowId(getClass(), _rowStatusTest, "Parameter 'rowStatusTest'");
		}
		if (_rowName == null) {
			indexName = null;
		} else {
			indexName = UtilsExcel.adaptAndCheckRowId(getClass(), _rowName, "Parameter 'rowName'");
		}
		if (_rowDescription == null) {
			indexDescription = null;
		} else {
			indexDescription = UtilsExcel.adaptAndCheckRowId(getClass(), _rowName, "Parameter 'rowDescription'");
		}
		if (_rowGroup == null) {
			indexGroup = null;
		} else {
			indexGroup = UtilsExcel.adaptAndCheckRowId(getClass(), _rowGroup, "Parameter 'rowGroup'");
		}
		if (_rowOrder == null) {
			indexOrder = null;
		} else {
			indexOrder = UtilsExcel.adaptAndCheckRowId(getClass(), _rowOrder, "Parameter 'rowOrder'");
		}

		Set<String> columns = new HashSet<String>();
		UtilsExcel.checkDoubles(columns, _columnParameter, "columnParameter");
		UtilsExcel.checkDoubles(columns, _columnIo, "columnIo");

		Set<Integer> rows = new HashSet<Integer>();
		UtilsExcel.checkDoubles(rows, _rowCode, "rowCode");
		UtilsExcel.checkDoubles(rows, _rowActive, "rowActive");
		UtilsExcel.checkDoubles(rows, _rowStatusTest, "rowStatusTest");
		UtilsExcel.checkDoubles(rows, _rowName, "rowName");
		UtilsExcel.checkDoubles(rows, _rowDescription, "rowDescription");
		UtilsExcel.checkDoubles(rows, _rowGroup, "rowGroup");
		UtilsExcel.checkDoubles(rows, _rowOrder, "rowOrder");

		configured = true;
	}

	public void testConfigured() {
		super.testConfigured();
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getColumnParameter() {
		return _columnParameter;
	}

	public void setColumnParameter(String columnParameter) {
		this._columnParameter = columnParameter;
	}

	public String getColumnIo() {
		return _columnIo;
	}

	public void setColumnIo(String columnIo) {
		this._columnIo = columnIo;
	}

	public Integer getRowCode() {
		return _rowCode;
	}

	public void setRowCode(Integer rowCode) {
		this._rowCode = rowCode;
	}

	public Integer getRowActive() {
		return _rowActive;
	}

	public void setRowActive(Integer rowActive) {
		this._rowActive = rowActive;
	}

	public Integer getRowName() {
		return _rowName;
	}

	public void setRowName(Integer rowName) {
		this._rowName = rowName;
	}

	public Integer getRowDescription() {
		return _rowDescription;
	}

	public void setRowDescription(Integer rowDescription) {
		this._rowDescription = rowDescription;
	}

	public Integer getRowGroup() {
		return _rowGroup;
	}

	public void setRowGroup(Integer rowGroup) {
		this._rowGroup = rowGroup;
	}

	public Integer getRowOrder() {
		return _rowOrder;
	}

	public void setRowOrder(Integer rowOrder) {
		this._rowOrder = rowOrder;
	}

	public Integer getRowStatusTest() {
		return _rowStatusTest;
	}

	public void setRowStatusTest(Integer rowStatusTest) {
		this._rowStatusTest = rowStatusTest;
	}
}
