package kkr.ktm.components.tablewriter.excel;

import java.io.File;

import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.exception.ConfigurationException;

public abstract class TableWriterExcelFwk {
	private boolean configured;

	protected ExcelAdapter excelAdapter;
	protected File dir;

	public void config() throws ConfigurationException {
		configured = false;
		if (excelAdapter == null) {
			throw new ConfigurationException("Parameter 'excelAdapter' is not configured");
		}
		if (dir == null) {
			throw new ConfigurationException("Parameter 'dir' is not configured");
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

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}
}
