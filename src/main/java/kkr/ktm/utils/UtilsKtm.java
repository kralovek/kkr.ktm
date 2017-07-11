package kkr.ktm.utils;

import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.errors.TestException;

public class UtilsKtm {
	public static String checkPrefix(String value, String name) throws ConfigurationException {
		String retval;
		if (UtilsString.isEmpty(value)) {
			retval = "";
		} else {
			if (!value.matches("[a-zA-Z_0-9]*")) {
				throw new ConfigurationException("Parameter '" + name + "' has bad value: " + value);
			}
			retval = value + ".";
		}
		return retval;
	}

	public static String checkEntityName(String value, String name) throws ConfigurationException {
		String retval;
		if (UtilsString.isEmpty(value)) {
			retval = null;
		} else {
			if (!value.matches("[a-zA-Z_0-9]*")) {
				throw new ConfigurationException("Parameter '" + name + "' has bad value: " + value);
			}
			retval = value;
		}
		return retval;
	}

	public static String checkInputString(TestInput testInput, String parameter) throws TestException {
		String retval;
		Object object = checkInputScalar(testInput, parameter);
		if (object == null) {
			retval = null;
		} else {
			retval = object.toString();
			if (UtilsString.isEmpty(retval)) {
				retval = null;
			}
		}
		return retval;
	}

	public static Boolean checkInputBoolean(TestInput testInput, String parameter) throws TestException {
		Boolean retval;
		Object object = checkInputScalar(testInput, parameter);
		if (object == null) {
			retval = null;
		} else if (object instanceof Boolean) {
			retval = (Boolean) object;
		} else if (object instanceof String) {
			String string = (String) object;
			if (UtilsString.isEmpty(string)) {
				retval = null;
			} else if ("true".equals(string)) {
				retval = true;
			} else if ("false".equals(string)) {
				retval = false;
			} else {
				throw new TestException(testInput, "Bad value of boolean parameter: " + string);
			}
		} else {
			throw new TestException(testInput, "Bad datatype of boolean parameter: " + object.getClass().getName());
		}
		return retval;
	}

	public static Integer checkInputInteger(TestInput testInput, String parameter) throws TestException {
		Integer retval;
		Object object = checkInputScalar(testInput, parameter);
		if (object == null) {
			retval = null;
		} else if (object instanceof Number) {
			retval = ((Number) object).intValue();
		} else if (object instanceof String) {
			String string = (String) object;
			if (UtilsString.isEmpty(string)) {
				retval = null;
			} else {
				try {
					retval = Integer.parseInt(string);
				} catch (Exception ex) {
					throw new TestException(testInput, parameter, "Bad value of integer parameter: " + string);
				}
			}
		} else {
			throw new TestException(testInput, parameter, "Bad datatype of integer parameter: " + object.getClass().getName());
		}
		return retval;
	}

	private static Object checkInputScalar(TestInput testInput, String parameter) throws TestException {
		Object retval;
		Object object = testInput.getDataInput().get(parameter);
		if (object == null) {
			retval = null;
		} else if (!object.getClass().isArray()) {
			retval = object;
		} else {
			String dimension = "";
			retval = object;
			while (retval != null && retval.getClass().isArray()) {
				Object[] array = (Object[]) object;
				dimension += "[" + array.length + "]";
				if (array.length == 0) {
					retval = null;
					break;
				} else if (array.length == 1) {
					retval = array[0];
				} else {
					throw new TestException(testInput, parameter, "Parameter should be scalar: " + dimension);
				}
			}
		}

		return retval;
	}
}
