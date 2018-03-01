package kkr.ktm.domains.excel.utils;

import java.util.Collection;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.excel.data.ExcelIdCell;

public class UtilsExcel implements ConstantsExcel {

	public static ExcelIdCell adaptAndCheckCellId(Class<?> clazz, String cellId, String name)
			throws ConfigurationException {
		if (cellId == null) {
			return null;
		}
		int i;
		for (i = 0; i < cellId.length() && Character.isLetter(cellId.charAt(i)); i++) {
		}
		String idColumnCell = cellId.substring(0, i);
		String idRowCell = cellId.substring(i);

		Integer idColumnExcelMin = columnToIndex(EXCEL_MIN_COLUMN);
		Integer idColumnExcelMax = columnToIndex(EXCEL_MAX_COLUMN);
		Integer columnIntId = columnToIndex(idColumnCell);
		Integer rowIntId;
		try {
			rowIntId = Integer.parseInt(idRowCell);
		} catch (NumberFormatException ex) {
			rowIntId = null;
		}

		if (columnIntId == null || rowIntId == null || //
				columnIntId < idColumnExcelMin || columnIntId > idColumnExcelMax || //
				rowIntId < EXCEL_MIN_ROW || rowIntId > EXCEL_MAX_ROW) {
			throw new ConfigurationException(
					clazz.getSimpleName() + ": Parameter '" + name + "' must be form the range: <" + EXCEL_MIN_COLUMN
							+ EXCEL_MIN_ROW + " - " + EXCEL_MAX_COLUMN + EXCEL_MAX_ROW + ">");
		}
		ExcelIdCell idCell = new ExcelIdCell();
		idCell.setColumn(columnIntId);
		idCell.setRow(rowIntId - 1);

		return idCell;
	}

	public static <T> void checkDoubles(Collection<T> data, T item, String parameter) throws ConfigurationException {
		if (item == null) {
			return;
		}
		if (data.contains(item)) {
			throw new ConfigurationException("Parameter '" + parameter + "' does not have unique value: " + item);
		}
		data.add(item);
	}

	public static Integer adaptAndCheckRowId(Class<?> clazz, Integer rowId, String name) throws ConfigurationException {
		if (rowId == null) {
			return null;
		}
		if (rowId < EXCEL_MIN_ROW || rowId > EXCEL_MAX_ROW) {
			throw new ConfigurationException(clazz.getSimpleName() + ": Parameter '" + name
					+ "' must be form the range: <" + EXCEL_MIN_ROW + " - " + EXCEL_MAX_ROW + ">");
		}
		return rowId - 1;
	}

	public static Integer adaptAndCheckColumnId(Class<?> clazz, String columnId, String name)
			throws ConfigurationException {
		if (isEmpty(columnId)) {
			return null;
		}
		Integer idColumnExcelMin = columnToIndex(EXCEL_MIN_COLUMN);
		Integer idColumnExcelMax = columnToIndex(EXCEL_MAX_COLUMN);
		Integer columnIntId = columnToIndex(columnId);

		if (columnIntId == null || columnIntId < idColumnExcelMin || columnIntId > idColumnExcelMax) {
			throw new ConfigurationException(clazz.getSimpleName() + ": Parameter '" + name
					+ "' must be form the range: <" + EXCEL_MIN_COLUMN + " - " + EXCEL_MAX_COLUMN + ">");
		}
		return columnIntId;
	}

	private static Integer columnToIndex(String pColumn) {
		if (pColumn == null || pColumn.length() == 0) {
			return null;
		}
		String column = pColumn.toUpperCase();

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

	protected static boolean isEmpty(String pValue) {
		return pValue == null || pValue.isEmpty();
	}
}
