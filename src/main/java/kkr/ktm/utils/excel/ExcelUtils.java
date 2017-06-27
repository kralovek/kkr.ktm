package kkr.ktm.utils.excel;

import java.util.Collection;

import kkr.ktm.exception.ConfigurationException;

public class ExcelUtils implements ExcelConstants {

	private static ExcelUtils excelUtils = new ExcelUtils();

	public static ExcelUtils getInstance() {
		return excelUtils;
	}

	public ExcelIdCell adaptAndCheckCellId(final String pCellId, final String pName) throws ConfigurationException {
		if (pCellId == null) {
			return null;
		}
		int i;
		for (i = 0; i < pCellId.length() && Character.isLetter(pCellId.charAt(i)); i++) {
		}
		final String idColumnCell = pCellId.substring(0, i);
		final String idRowCell = pCellId.substring(i);

		final Integer idColumnExcelMin = columnToIndex(EXCEL_MIN_COLUMN);
		final Integer idColumnExcelMax = columnToIndex(EXCEL_MAX_COLUMN);
		final Integer columnIntId = columnToIndex(idColumnCell);
		Integer rowIntId;
		try {
			rowIntId = Integer.parseInt(idRowCell);
		} catch (final NumberFormatException ex) {
			rowIntId = null;
		}

		if (columnIntId == null || rowIntId == null || //
				columnIntId < idColumnExcelMin || columnIntId > idColumnExcelMax || //
				rowIntId < EXCEL_MIN_ROW || rowIntId > EXCEL_MAX_ROW) {
			throw new ConfigurationException(getClass().getSimpleName() + ": " + pName + " must be form the range: <" + EXCEL_MIN_COLUMN
					+ EXCEL_MIN_ROW + " - " + EXCEL_MAX_COLUMN + EXCEL_MAX_ROW + ">");
		}
		final ExcelIdCell idCell = new ExcelIdCell();
		idCell.setColumn(columnIntId);
		idCell.setRow(rowIntId - 1);

		return idCell;
	}

	public <T> void checkDoubles(Collection<T> data, T item, String parameter) throws ConfigurationException {
		if (item == null) {
			return;
		}
		if (data.contains(item)) {
			throw new ConfigurationException("Parameter '" + parameter + "' does not have unique value: " + item);
		}
		data.add(item);
	}

	public Integer adaptAndCheckRowId(final Integer pRowId, final String pName) throws ConfigurationException {
		if (pRowId == null) {
			return null;
		}
		if (pRowId < EXCEL_MIN_ROW || pRowId > EXCEL_MAX_ROW) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": " + pName + " must be form the range: <" + EXCEL_MIN_ROW + " - " + EXCEL_MAX_ROW + ">");
		}
		return pRowId - 1;
	}

	public Integer adaptAndCheckColumnId(final String pColumnId, final String pName) throws ConfigurationException {
		if (isEmpty(pColumnId)) {
			return null;
		}
		final Integer idColumnExcelMin = columnToIndex(EXCEL_MIN_COLUMN);
		final Integer idColumnExcelMax = columnToIndex(EXCEL_MAX_COLUMN);
		final Integer columnIntId = columnToIndex(pColumnId);

		if (columnIntId == null || columnIntId < idColumnExcelMin || columnIntId > idColumnExcelMax) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": " + pName + " must be form the range: <" + EXCEL_MIN_COLUMN + " - " + EXCEL_MAX_COLUMN + ">");
		}
		return columnIntId;
	}

	private static Integer columnToIndex(String pColumn) {
		if (pColumn == null || pColumn.length() == 0) {
			return null;
		}
		final String column = pColumn.toUpperCase();

		int retval = 0;
		int exp = 1;
		for (int i = 0; i < column.length(); i++) {
			char c = column.charAt(column.length() - 1 - i);
			if (!Character.isLetter(c)) {
				return null;
			}
			int cindex = c - 'A' + 1;
			retval += cindex * exp;
			exp *= 26;
		}
		return retval - 1;
	}

	protected boolean isEmpty(final String pValue) {
		return pValue == null || pValue.isEmpty();
	}

}
