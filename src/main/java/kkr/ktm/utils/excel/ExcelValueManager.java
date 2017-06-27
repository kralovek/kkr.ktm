package kkr.ktm.utils.excel;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kkr.ktm.exception.BaseException;

public class ExcelValueManager {

	private static final int CELL_MAX_SIZE = 32767;
	private static final String CELL_MAX_SIZE_CUT = "(CUT)";

	private DateFormat dateFormat;

	public ExcelValueManager(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Value excelToFlagTestValue(ExcelPosition excelPosition, Object value)
			throws BaseException {

		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			String strValue = (String) value;
			try {
				Value valueIntern = ValueParser.parseValue(strValue);
				return valueIntern;
			} catch (Exception ex) {
				throw new ExcelConfigurationException(excelPosition,
						"Bad format of the value: " + ex.getMessage());
			}
		} else {
			Value valueIntern = new Value();
			valueIntern.setValue(value);
			return valueIntern;
		}
	}

	public Object excelToTestValue(ExcelPosition excelPosition, Object value)
			throws BaseException {

		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			String strValue = (String) value;
			try {
				Value valueIntern = ValueParser.parseValue(strValue);
				return valueIntern.getValue();
			} catch (Exception ex) {
				throw new ExcelConfigurationException(excelPosition,
						"Bad format of the value: " + ex.getMessage());
			}
		} else {
			return value;
		}
	}

	public Object testToExcelValue(ExcelPosition excelPosition, Object value) {
		Object object;
		for (object = value; object != null && object.getClass().isArray()
				&& ((Object[]) object).length == 1; object = ((Object[]) object)[0])
			;

		if (object == null) {
			return null;
		}
		if (!object.getClass().isArray()) {
			return value;
		}

		return testToExcelStringValue(excelPosition, value);
	}

	public String testToExcelStringValue(ExcelPosition excelPosition,
			Object value) {
		if (value == null) {
			return "";
		}
		String retval;
		if (!value.getClass().isArray()) {
			retval = value.toString();
			if (retval.length() > CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length()) {
				retval = retval.substring(0,
						CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length());
			}
			return retval;
		} else {
			final Object[] array = (Object[]) value;
			final StringBuffer buffer = new StringBuffer();
			int i = 0;
			for (final Object object : array) {
				if (i != 0) {
					buffer.append("|");
				}
				buffer.append(testToExcelStringValue(excelPosition, object));
				i++;
			}
			if (buffer.length() > CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length()
					- 2) {
				return "["
						+ buffer.substring(0,
								CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length() - 2)
						+ "]";
			} else {
				return "[" + buffer.toString() + "]";
			}
		}
	}

	//
	// ##############################################################################
	//

	public boolean compareTestValues(ExcelPosition excelPositionE,
			Value valueFlagE, Object valueO) throws ExcelConfigurationException {
		Object valueE = valueFlagE != null && valueFlagE.getValue() != null ? valueFlagE
				.getValue() : null;
		boolean flagIORD = valueFlagE != null ? valueFlagE.getFlags().contains(
				Value.FLAG_IORD) : false;
		boolean result = compareValues(excelPositionE, valueE, valueO, flagIORD);
		return result;
	}

	private boolean compareValues(ExcelPosition excelPositionE,
			Object valueOrPatternExpected, Object valueOutput,
			boolean ignorOrder) throws ExcelConfigurationException {
		boolean emptyE = isEmpty(valueOrPatternExpected);
		boolean emptyO = isEmpty(valueOutput);
		if (emptyE && emptyO) {
			return true;
		}
		if (emptyE) {
			valueOrPatternExpected = "";
		}
		if (emptyO) {
			valueOutput = "";
		}

		int sizeE = !valueOrPatternExpected.getClass().isArray() ? 1
				: ((Object[]) valueOrPatternExpected).length;
		int sizeO = !valueOutput.getClass().isArray() ? 1
				: ((Object[]) valueOutput).length;
		if (sizeE != sizeO) {
			return false;
		}
		if (sizeE > 1) {
			Object[] arrayE = (Object[]) valueOrPatternExpected;
			Object[] arrayO = (Object[]) valueOutput;
			if (ignorOrder) {
				return compareValuesArraysIgnorOrder(excelPositionE, arrayE, arrayO);
			} else {
				return compareValuesArraysRespectOrder(excelPositionE, arrayE, arrayO);
			}
		} else if (sizeE == 1) {
			Object valueE = null;

			if (valueOrPatternExpected.getClass().isArray()) {
				Object object = ((Object[]) valueOrPatternExpected)[0];
				if (object != null && object.getClass().isArray()) {
					if (((Object[]) object).length == 1) {
						object = ((Object[]) object)[0];
					} else {
						return false;
					}
				}

				valueE = object;
			} else {
				valueE = valueOrPatternExpected;
			}

			final Object valueO = valueOutput.getClass().isArray() ? ((Object[]) valueOutput)[0]
					: valueOutput;

			return compareSingleValues(excelPositionE, valueE, valueO);
		}
		return true;
	}

	private boolean compareSingleValues(ExcelPosition excelPosition,
			Object valueE, Object valueO) throws ExcelConfigurationException {
		if (valueE instanceof String && valueO instanceof String) {
			return compareValuesString(excelPosition, (String) valueE,
					(String) valueO);
		}
		if (valueE instanceof Number && valueO instanceof Number) {
			return ((Number) valueE).doubleValue() == ((Number) valueO)
					.doubleValue();
		}
		if (valueE instanceof Date && valueO instanceof Date) {
			return ((Date) valueE).compareTo((Date) valueO) == 0;
		}
		if (valueE instanceof Number && valueO instanceof String) {
			Double dblValueO = toDouble((String) valueO);
			return dblValueO != null
					&& ((Number) valueE).doubleValue() == dblValueO
							.doubleValue();
		}
		if (valueO instanceof Number && valueE instanceof String) {
			Double dblValueE = toDouble((String) valueE);
			return dblValueE != null
					&& ((Number) valueO).doubleValue() == dblValueE
							.doubleValue();
		}
		if (valueE instanceof Date && valueO instanceof String) {
			Date datValueO = toDate((String) valueO);
			return datValueO != null
					&& ((Date) valueE).compareTo(datValueO) == 0;
		}
		if (valueO instanceof Date && valueE instanceof String) {
			Date datValueE = toDate((String) valueE);
			return datValueE != null
					&& ((Date) valueO).compareTo(datValueE) == 0;
		}
		return false;
	}

	private Date toDate(String value) {
		try {
			return dateFormat.parse(value);
		} catch (Exception ex) {
			return null;
		}
	}

	private Double toDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private boolean compareValuesArraysRespectOrder(
			ExcelPosition excelPositionE, Object[] arrayE, Object[] arrayO)
			throws ExcelConfigurationException {
		for (int i = 0; i < arrayE.length; i++) {
			boolean compared = compareValues(excelPositionE, arrayE[i],
					arrayO[i], false);
			if (!compared) {
				return false;
			}
		}
		return true;
	}

	private boolean compareValuesArraysIgnorOrder(ExcelPosition excelPositionE, Object[] arrayE,
			Object[] arrayO) throws ExcelConfigurationException {
		Set<Integer> usedIndexesO = new HashSet<Integer>();
		for (int iE = 0; iE < arrayE.length; iE++) {
			boolean result = false;
			for (int iO = 0; iO < arrayO.length && !result; iO++) {
				if (usedIndexesO.contains(iO)) {
					continue;
				}
				result = compareValues(excelPositionE, arrayE[iE], arrayO[iO], true);
				if (result) {
					usedIndexesO.add(iO);
				}
			}
			if (!result) {
				return false;
			}
		}
		return true;
	}

	private boolean compareValuesString(ExcelPosition excelPosition,
			String valueOrPattern, final String pValue2)
			throws ExcelConfigurationException {
		Pattern pattern1 = valueToPattern(excelPosition, valueOrPattern);
		String value2 = adaptValue(pValue2);
		if (pattern1 != null) {
			boolean result = pattern1.matcher(value2).matches();
			return result;
		} else {
			boolean result = valueOrPattern.equals(value2);
			return result;
		}
	}

	private String adaptValue(String value) {
		if (value == null) {
			return value;
		}
		return value.replace("\r\n", " ").replace("\n", " ");
	}

	private Pattern valueToPattern(ExcelPosition excelPosition, String value)
			throws ExcelConfigurationException {
		if (value.startsWith("{") && value.endsWith("}")
				&& !value.endsWith("\\}")) {
			String strPattern = value.substring(1, value.length() - 1);
			try {
				return Pattern.compile(strPattern);
			} catch (PatternSyntaxException ex) {
				throw new ExcelConfigurationException(excelPosition,
						"The pattern is incorect: " + strPattern, ex);
			}
		}
		return null;
	}

	protected boolean isEmpty(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			if (array.length == 0) {
				return true;
			}
			if (array.length == 1 && "".equals(array[0])) {
				return true;
			}
			return false;
		}
		return object.toString().isEmpty();
	}

	//
	// ##############################################################################
	//
}
