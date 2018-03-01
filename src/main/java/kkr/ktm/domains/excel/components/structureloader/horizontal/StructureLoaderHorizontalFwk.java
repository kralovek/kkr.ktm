package kkr.ktm.domains.excel.components.structureloader.horizontal;

import java.util.HashSet;
import java.util.Set;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.excel.components.structureloader.base.StructureLoaderBase;
import kkr.ktm.domains.excel.utils.UtilsExcel;

public abstract class StructureLoaderHorizontalFwk extends StructureLoaderBase {
	private boolean configured;

	private Integer _rowParameter;
	private Integer _rowIo;

	public void config() throws ConfigurationException {
		configured = false;
		super.config();

		if (_rowParameter == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'rowParameter' is not configured");
		} else {
			indexParameter = UtilsExcel.adaptAndCheckRowId(getClass(), _rowParameter, "rowParameter");
		}
		if (_rowIo == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'rowIo' is not configured");
		} else {
			indexIo = UtilsExcel.adaptAndCheckRowId(getClass(), _rowIo, "rowIo");
		}

		Set<Integer> rows = new HashSet<Integer>();
		UtilsExcel.checkDoubles(rows, _rowParameter, "rowParameter");
		UtilsExcel.checkDoubles(rows, _rowIo, "rowIo");

		indexesParametersIgnored = columnsIgnored;
		indexesTestsIgnored = rowsIgnored;

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
}
