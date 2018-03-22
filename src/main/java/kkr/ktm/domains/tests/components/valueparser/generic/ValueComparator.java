package kkr.ktm.domains.tests.components.valueparser.generic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import kkr.ktm.domains.tests.components.valueparser.Flag;

public abstract class ValueComparator extends ValueParserGenericFwk {

	public boolean compareValue(Object valueO, Object valueE, Collection<Flag> flags) {
		valueO = reduceArray(valueO);
		valueE = reduceArray(valueE);

		if (valueO == null && valueE == null) {
			return true;
		}

		if (valueO == null || valueE == null) {
			return false;
		}

		if (valueO.getClass().isArray() != valueE.getClass().isArray()) {
			return false;
		}

		if (valueO.getClass().isArray()) {
			return compareArrays((Object[]) valueO, (Object[]) valueE, flags);
		}

		if (valueE instanceof Pattern) {
			return compareStringPattern(valueO, (Pattern) valueE, flags);
		}

		if (valueO instanceof String) {
			return compareStringObject((String) valueO, valueE, flags);
		}

		if (valueE instanceof String) {
			return compareStringObject((String) valueE, valueO, flags);
		}

		if (valueO instanceof Number && valueE instanceof Number) {
			return new Double(((Number) valueO).doubleValue()).equals(((Number) valueE).doubleValue());
		}

		return valueO.equals(valueE);
	}

	private boolean compareStringPattern(Object valueO, Pattern patternE, Collection<Flag> flags) {
		if (valueO instanceof String) {
			return patternE.matcher((String) valueO).matches();
		}
		return false;
	}

	private boolean compareStringObject(String string, Object object, Collection<Flag> flags) {
		if (object instanceof Date) {
			return compareStringDate(string, (Date) object, flags);
		} else if (object instanceof Number) {
			return compareStringNumber(string, (Number) object, flags);
		} else if (object instanceof Boolean) {
			return compareStringBoolean(string, (Boolean) object, flags);
		} else if (object instanceof String) {
			return compareStrings(string, (String) object, flags);
		} else {
			throw new IllegalArgumentException("Unsupported data type to compare: " + object.getClass().getName());
		}
	}

	private boolean compareStrings(String string1, String string2, Collection<Flag> flags) {
		if (flags.contains(Flag.NOCASE)) {
			return string1.equalsIgnoreCase(string2);
		} else {
			return string1.equals(string2);
		}
	}

	private boolean compareStringDate(String string, Date date, Collection<Flag> flags) {
		for (DateFormat dateFormat : dateFormats) {
			try {
				return dateFormat.parse(string).equals(date);
			} catch (ParseException ex) {
				// Nothing to do
			}
		}
		return false;
	}

	private boolean compareStringNumber(String string, Number number, Collection<Flag> flags) {
		return Double.parseDouble(string.replace(',', '.')) == number.doubleValue();
	}

	private boolean compareStringBoolean(String string, Boolean bool, Collection<Flag> flags) {
		return Boolean.parseBoolean(string) == bool;
	}

	private boolean compareArrays(Object[] arrayO, Object[] arrayE, Collection<Flag> flags) {
		if (arrayO.length != arrayE.length) {
			return false;
		}
		boolean result = true;
		for (int i = 0; i < arrayO.length; i++) {
			if (!compareValue(arrayO[i], arrayE[i], flags)) {
				result = false;
				break;
			}
		}
		if (result) {
			return true;
		}
		if (flags.contains(Flag.NOORDER)) {
			compareArraysNoOrder(arrayO, arrayE, flags);
		}
		return false;
	}

	private boolean compareArraysNoOrder(Object[] arrayO, Object[] arrayE, Collection<Flag> flags) {
		boolean[] historyE = new boolean[arrayE.length];
		loopO: for (int iO = 0; iO < arrayO.length; iO++) {
			for (int iE = 0; iE < arrayE.length; iE++) {
				if (historyE[iE]) {
					continue;
				}
				if (compareValue(arrayO[iO], arrayE[iE], flags)) {
					historyE[iE] = true;
					continue loopO;
				}
			}
			return false;
		}
		return true;
	}

	private Object reduceArray(Object value) {
		if (value != null) {
			if (value instanceof String && ((String) value).isEmpty()) {
				return null;
			} else if (value.getClass().isArray()) {
				Object[] array = (Object[]) value;
				if (array.length == 1) {
					return reduceArray(array[0]);
				} else if (array.length == 0) {
					return null;
				}
			}
		}
		return value;
	}
}
