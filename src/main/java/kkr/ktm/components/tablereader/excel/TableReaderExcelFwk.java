package kkr.ktm.components.tablereader.excel;

import java.io.File;

import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.exception.ConfigurationException;

public abstract class TableReaderExcelFwk {
	private boolean configured;

	protected String sheet;
	protected File dir;
	protected ExcelAdapter excelAdapter;

	public void config() throws ConfigurationException {
		configured = false;
		if (sheet == null) {
			// OK - the first sheet
		}
		if (dir == null) {
			throw new ConfigurationException("Parameter 'dir' is not configured");
		}
		if (excelAdapter == null) {
			throw new ConfigurationException("Parameter 'excelAdapter' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public ExcelAdapter getExcelAdapter() {
		return excelAdapter;
	}

	public void setExcelAdapter(ExcelAdapter excelAdapter) {
		this.excelAdapter = excelAdapter;
	}
}
