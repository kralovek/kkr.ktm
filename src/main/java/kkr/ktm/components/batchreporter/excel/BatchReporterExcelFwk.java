package kkr.ktm.components.batchreporter.excel;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.exception.ConfigurationException;

public abstract class BatchReporterExcelFwk {
	private boolean configured;

	protected ExcelAdapter excelAdapter;

	protected File dir;

	protected Boolean printResults;
	private String maskIgnoreName;
	protected Pattern patternIgnorName;

	public void config() throws ConfigurationException {
		configured = false;
		if (excelAdapter == null) {
			throw new ConfigurationException(
					"The parameter 'excelAdapter' is not configured.");
		}
		if (dir == null) {
			throw new ConfigurationException(
					"The parameter 'dir' is not configured.");
		}
		if (maskIgnoreName != null) {
			try {
				patternIgnorName = Pattern.compile(maskIgnoreName);
			} catch (PatternSyntaxException ex) {
				throw new ConfigurationException(
						"The parameter 'patternIgnorName' is not valid regexp pattern: "
								+ maskIgnoreName, ex);
			}
		}
		if (printResults == null) {
			printResults = false;
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
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

	public String getMaskIgnoreName() {
		return maskIgnoreName;
	}

	public void setMaskIgnoreName(String maskIgnoreName) {
		this.maskIgnoreName = maskIgnoreName;
	}

	public Boolean getPrintResults() {
		return printResults;
	}

	public void setPrintResults(Boolean printResults) {
		this.printResults = printResults;
	}

}
