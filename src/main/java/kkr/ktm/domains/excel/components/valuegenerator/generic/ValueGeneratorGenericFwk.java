package kkr.ktm.domains.excel.components.valuegenerator.generic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.common.errors.ConfigurationException;

public abstract class ValueGeneratorGenericFwk {
	private boolean configured;

	private String _dateFormat;
	protected DateFormat dateFormat;

	protected ExcelAdapter excelAdapter;

	public void config() throws ConfigurationException {
		configured = false;
		if (excelAdapter == null) {
			throw new ConfigurationException("Parameter 'excelAdapter' is not configured");
		}
		if (_dateFormat == null) {
			throw new ConfigurationException("Parameter 'dateFormat' is not configured");
		} else {
			try {
				dateFormat = new SimpleDateFormat(_dateFormat);
				dateFormat.format(new Date());
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'dateFormat' has bad value: " + _dateFormat);
			}
		}
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

	public String getDateFormat() {
		return _dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this._dateFormat = dateFormat;
	}
}
