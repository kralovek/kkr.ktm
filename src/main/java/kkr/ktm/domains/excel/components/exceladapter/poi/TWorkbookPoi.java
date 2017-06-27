package kkr.ktm.domains.excel.components.exceladapter.poi;

import java.io.File;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;

public class TWorkbookPoi implements TWorkbook {

	private File file;
	private Workbook workbook;
	private InputStream inputStream;
	private FormulaEvaluator evaluator;

	private short formatDate;
	private short formatTime;
	private short formatInteger;
	private short formatDouble;

	public TWorkbookPoi(Workbook workbook, File file) {
		if (workbook == null) {
			throw new IllegalArgumentException("Workbook is null");
		}
		if (file == null) {
			throw new IllegalArgumentException("File is null");
		}
		this.file = file;
		this.workbook = workbook;

		if (workbook instanceof XSSFWorkbook) {
			evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
		} else if (workbook instanceof HSSFWorkbook) {
			evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
		} else {
			throw new IllegalStateException("Unknown type of Workbook: " + workbook.getClass().getSimpleName());
		}
	}

	public FormulaEvaluator getEvaluator() {
		return evaluator;
	}

	public File getFile() {
		return file;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public short getFormatDate() {
		return formatDate;
	}

	public void setFormatDate(short formatDate) {
		this.formatDate = formatDate;
	}

	public short getFormatTime() {
		return formatTime;
	}

	public void setFormatTime(short formatTime) {
		this.formatTime = formatTime;
	}

	public short getFormatInteger() {
		return formatInteger;
	}

	public void setFormatInteger(short formatInteger) {
		this.formatInteger = formatInteger;
	}

	public short getFormatDouble() {
		return formatDouble;
	}

	public void setFormatDouble(short formatDouble) {
		this.formatDouble = formatDouble;
	}

	public String toString() {
		return file.getAbsolutePath();
	}
}
