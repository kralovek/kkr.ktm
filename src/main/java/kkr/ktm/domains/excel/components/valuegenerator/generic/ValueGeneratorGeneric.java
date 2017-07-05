package kkr.ktm.domains.excel.components.valuegenerator.generic;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kkr.ktm.domains.excel.components.valuegenerator.ValueGenerator;
import kkr.ktm.domains.tests.data.ValueFlag;
import kkr.ktm.domains.tests.data.ValuePattern;
import kkr.common.errors.BaseException;
import kkr.common.errors.ExcelException;
import kkr.common.utils.excel.ExcelPosition;

public class ValueGeneratorGeneric extends ValueGeneratorGenericFwk implements ValueGenerator {

	private static final int CELL_MAX_SIZE = 32767;
	private static final String CELL_MAX_SIZE_CUT = "(CUT)";

	public ValuePattern parsePattern(ExcelPosition excelPosition, Object value) throws BaseException {

		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			String strValue = (String) value;
			try {
				ValuePattern valueIntern = ValueParser.parseValue(strValue);
				return valueIntern;
			} catch (Exception ex) {
				throw new ExcelException(excelPosition, "Bad format of the value: " + ex.getMessage());
			}
		} else {
			ValuePatternImpl valueIntern = new ValuePatternImpl();
			valueIntern.setValue(value);
			return valueIntern;
		}
	}

	public Object parseValue(ExcelPosition excelPosition, Object value) throws BaseException {

		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			String strValue = (String) value;
			try {
				ValuePattern valueIntern = ValueParser.parseValue(strValue);
				return valueIntern.getValue();
			} catch (Exception ex) {
				throw new ExcelException(excelPosition, "Bad format of the value: " + ex.getMessage());
			}
		} else {
			return value;
		}
	}

	public Object formatValue(ExcelPosition excelPosition, Object value) {
		Object object;
		for (object = value; object != null && object.getClass().isArray() && ((Object[]) object).length == 1; object = ((Object[]) object)[0]);

		if (object == null) {
			return null;
		}
		if (!object.getClass().isArray()) {
			return value;
		}

		return formatValueString(excelPosition, value);
	}

	private String formatValueString(ExcelPosition excelPosition, Object value) {
		if (value == null) {
			return "";
		}
		String retval;
		if (!value.getClass().isArray()) {
			retval = value.toString();
			if (retval.length() > CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length()) {
				retval = retval.substring(0, CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length());
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
				buffer.append(formatValueString(excelPosition, object));
				i++;
			}
			if (buffer.length() > CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length() - 2) {
				return "[" + buffer.substring(0, CELL_MAX_SIZE - CELL_MAX_SIZE_CUT.length() - 2) + "]";
			} else {
				return "[" + buffer.toString() + "]";
			}
		}
	}

	//
	// ##############################################################################
	//

	public boolean compareValues(ExcelPosition excelPositionE, ValuePattern valuePatternE, Object valueO) throws ExcelException {
		Object valueE = valuePatternE != null && valuePatternE.getValue() != null ? valuePatternE.getValue() : null;
		boolean flagIORD = valuePatternE != null ? valuePatternE.getFlags().contains(ValueFlag.IORD) : false;
		boolean result = compareValues(excelPositionE, valueE, valueO, flagIORD);
		return result;
	}

	private boolean compareValues(ExcelPosition excelPositionE, Object valueOrPatternExpected, Object valueOutput, boolean ignorOrder)
			throws ExcelException {
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

		int sizeE = !valueOrPatternExpected.getClass().isArray() ? 1 : ((Object[]) valueOrPatternExpected).length;
		int sizeO = !valueOutput.getClass().isArray() ? 1 : ((Object[]) valueOutput).length;
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

			final Object valueO = valueOutput.getClass().isArray() ? ((Object[]) valueOutput)[0] : valueOutput;

			return compareSingleValues(excelPositionE, valueE, valueO);
		}
		return true;
	}

	private boolean compareSingleValues(ExcelPosition excelPosition, Object valueE, Object valueO) throws ExcelException {
		if (valueE instanceof String && valueO instanceof String) {
			return compareValuesString(excelPosition, (String) valueE, (String) valueO);
		}
		if (valueE instanceof Number && valueO instanceof Number) {
			return ((Number) valueE).doubleValue() == ((Number) valueO).doubleValue();
		}
		if (valueE instanceof Date && valueO instanceof Date) {
			return ((Date) valueE).compareTo((Date) valueO) == 0;
		}
		if (valueE instanceof Number && valueO instanceof String) {
			Double dblValueO = toDouble((String) valueO);
			return dblValueO != null && ((Number) valueE).doubleValue() == dblValueO.doubleValue();
		}
		if (valueO instanceof Number && valueE instanceof String) {
			Double dblValueE = toDouble((String) valueE);
			return dblValueE != null && ((Number) valueO).doubleValue() == dblValueE.doubleValue();
		}
		if (valueE instanceof Date && valueO instanceof String) {
			Date datValueO = toDate((String) valueO);
			return datValueO != null && ((Date) valueE).compareTo(datValueO) == 0;
		}
		if (valueO instanceof Date && valueE instanceof String) {
			Date datValueE = toDate((String) valueE);
			return datValueE != null && ((Date) valueO).compareTo(datValueE) == 0;
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

	private boolean compareValuesArraysRespectOrder(ExcelPosition excelPositionE, Object[] arrayE, Object[] arrayO)
			throws ExcelException {
		for (int i = 0; i < arrayE.length; i++) {
			boolean compared = compareValues(excelPositionE, arrayE[i], arrayO[i], false);
			if (!compared) {
				return false;
			}
		}
		return true;
	}

	private boolean compareValuesArraysIgnorOrder(ExcelPosition excelPositionE, Object[] arrayE, Object[] arrayO) throws ExcelException {
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

	private boolean compareValuesString(ExcelPosition excelPosition, String valueOrPattern, final String pValue2) throws ExcelException {
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

	private Pattern valueToPattern(ExcelPosition excelPosition, String value) throws ExcelException {
		if (value.startsWith("{") && value.endsWith("}") && !value.endsWith("\\}")) {
			String strPattern = value.substring(1, value.length() - 1);
			try {
				return Pattern.compile(strPattern);
			} catch (PatternSyntaxException ex) {
				throw new ExcelException(excelPosition, "The _pattern is incorect: " + strPattern, ex);
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
}
