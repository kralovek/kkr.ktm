package kkr.ktm.domains.excel.components.structureloader.horizontal;

import java.util.HashSet;
import java.util.Set;

import kkr.ktm.domains.excel.components.structureloader.base.StructureLoaderBase;
import kkr.ktm.domains.excel.utils.UtilsExcel;
import kkr.common.errors.ConfigurationException;

public abstract class StructureLoaderHorizontalFwk extends StructureLoaderBase {
	private boolean configured;

	private Integer _rowParameter;
	private Integer _rowIo;
	private String _columnCode;
	private String _columnActive;
	private String _columnName;
	private String _columnDescription;
	private String _columnGroup;
	private String _columnOrder;
	private String _columnStatusTest;

	public void config() throws ConfigurationException {
		configured = false;
		super.config();

		if (_rowParameter == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'rowParameter' is not configured");
		} else {
			indexParameter = UtilsExcel.adaptAndCheckRowId(getClass(), _rowParameter, "Parameter 'rowParameter'");
		}
		if (_rowIo == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'rowIo' is not configured");
		} else {
			indexIo = UtilsExcel.adaptAndCheckRowId(getClass(), _rowIo, "Parameter 'rowIo'");
		}
		if (_columnCode == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'columnCode' is not configured");
		} else {
			indexCode = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnCode, "Parameter 'columnCode'");
		}
		if (_columnActive == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'columnActive' is not configured");
		} else {
			indexActive = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnActive, "Parameter 'columnActive'");
		}
		if (_columnStatusTest == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'columnStatusTest' is not configured");
		} else {
			indexStatusTest = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnStatusTest, "Parameter 'columnStatusTest'");
		}
		if (_columnName == null) {
			indexName = null;
		} else {
			indexName = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnName, "Parameter 'columnName'");
		}
		if (_columnDescription == null) {
			indexDescription = null;
		} else {
			indexDescription = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnName, "Parameter 'columnDescription'");
		}
		if (_columnGroup == null) {
			indexGroup = null;
		} else {
			indexGroup = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnGroup, "Parameter 'columnGroup'");
		}
		if (_columnOrder == null) {
			indexOrder = null;
		} else {
			indexOrder = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnOrder, "Parameter 'columnOrder'");
		}

		Set<Integer> rows = new HashSet<Integer>();
		UtilsExcel.checkDoubles(rows, _rowParameter, "rowParameter");
		UtilsExcel.checkDoubles(rows, _rowIo, "rowIo");

		Set<String> columns = new HashSet<String>();
		UtilsExcel.checkDoubles(columns, _columnCode, "columnCode");
		UtilsExcel.checkDoubles(columns, _columnActive, "columnActive");
		UtilsExcel.checkDoubles(columns, _columnStatusTest, "columnStatusTest");
		UtilsExcel.checkDoubles(columns, _columnName, "columnName");
		UtilsExcel.checkDoubles(columns, _columnDescription, "columnDescription");
		UtilsExcel.checkDoubles(columns, _columnOrder, "columnOrder");

		configured = true;
	}

	public void testConfigured() {
		super.testConfigured();
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Integer getRowParameter() {
		return _rowParameter;
	}

	public void setRowParameter(Integer rowParameter) {
		this._rowParameter = rowParameter;
	}

	public Integer getRowIo() {
		return _rowIo;
	}

	public void setRowIo(Integer rowIo) {
		this._rowIo = rowIo;
	}

	public String getColumnCode() {
		return _columnCode;
	}

	public void setColumnCode(String columnCode) {
		this._columnCode = columnCode;
	}

	public String getColumnActive() {
		return _columnActive;
	}

	public void setColumnActive(String columnActive) {
		this._columnActive = columnActive;
	}

	public String getColumnName() {
		return _columnName;
	}

	public void setColumnName(String columnName) {
		this._columnName = columnName;
	}

	public String getColumnDescription() {
		return _columnDescription;
	}

	public void setColumnDescription(String columnDescription) {
		this._columnDescription = columnDescription;
	}

	public String getColumnGroup() {
		return _columnGroup;
	}

	public void setColumnGroup(String columnGroup) {
		this._columnGroup = columnGroup;
	}

	public String getColumnOrder() {
		return _columnOrder;
	}

	public void setColumnOrder(String columnOrder) {
		this._columnOrder = columnOrder;
	}

	public String getColumnStatusTest() {
		return _columnStatusTest;
	}

	public void setColumnStatusTest(String columnStatusTest) {
		this._columnStatusTest = columnStatusTest;
	}
}
