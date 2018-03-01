package kkr.ktm.domains.excel.components.structureloader.vertical;

import java.util.HashSet;
import java.util.Set;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.excel.components.structureloader.base.StructureLoaderBase;
import kkr.ktm.domains.excel.utils.UtilsExcel;

public abstract class StructureLoaderVerticalFwk extends StructureLoaderBase {
	private boolean configured;

	private String _columnParameter;
	private String _columnIo;

	public void config() throws ConfigurationException {
		configured = false;
		super.config();

		if (_columnParameter == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'columnParameter' is not configured");
		} else {
			indexParameter = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnParameter, "columnParameter");
		}
		if (_columnIo == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'columnIo' is not configured");
		} else {
			indexIo = UtilsExcel.adaptAndCheckColumnId(getClass(), _columnIo, "columnIo");
		}

		Set<String> columns = new HashSet<String>();
		UtilsExcel.checkDoubles(columns, _columnParameter, "columnParameter");
		UtilsExcel.checkDoubles(columns, _columnIo, "columnIo");

		indexesParametersIgnored = rowsIgnored;
		indexesTestsIgnored = columnsIgnored;

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
}
