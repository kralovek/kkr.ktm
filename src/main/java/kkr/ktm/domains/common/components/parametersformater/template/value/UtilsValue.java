package kkr.ktm.domains.common.components.parametersformater.template.value;

import java.util.Date;

public class UtilsValue {

	public static boolean isValidValue(Object value) {
		return value == null //
				|| value instanceof Number //
				|| value instanceof String //
				|| value instanceof Boolean //
				|| value instanceof Date //
		;
	}

	public static Double toDouble(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof Number) {
			return ((Number) object).doubleValue();
		}
		if (object instanceof Boolean) {
			return ((Boolean) object) ? 1. : 0;
		}
		if (object instanceof String) {
			try {
				return Double.parseDouble((String) object);
			} catch (NumberFormatException ex) {
				try {
					return Boolean.parseBoolean((String) object) ? 1.0 : 0;
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}

	public static Integer toInteger(Object object) {
		Double d = toDouble(object);
		if (d == null) {
			return null;
		}
		return d.intValue() == (int) d.doubleValue() ? d.intValue() : null;
	}
}
