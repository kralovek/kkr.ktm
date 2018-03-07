package kkr.ktm.domains.common.components.parametersformater.template.value;

import java.util.Comparator;
import java.util.Date;

public class ComparatorValue implements Comparator<Object> {

	private Double toDouble(Object object) {
		Double d = UtilsValue.toDouble(object);
		if (d == null) {
			throw new IllegalArgumentException("Object cannot be coverted to a number: " + object);
		}
		return d;
	}

	public boolean equals(Object object1, Object object2) {
		if (object1 == null) {
			throw new IllegalArgumentException("Object1 is null");
		}
		if (object2 == null) {
			throw new IllegalArgumentException("Object2 is null");
		}

		if (object1 instanceof String && object2 instanceof String) {
			return object1.equals(object2);
		}
		return compare(object1, object2) == 0;
	}

	public int compare(Object object1, Object object2) {
		if (UtilsValue.isValidValue(object1)) {
			throw new IllegalArgumentException("Unsupported valule: " + String.valueOf(object1));
		}
		if (UtilsValue.isValidValue(object2)) {
			throw new IllegalArgumentException("Unsupported valule: " + String.valueOf(object2));
		}

		if (object1 instanceof Date && object2 instanceof Date) {
			return ((Date) object1).compareTo((Date) object2);
		}

		if (object1 instanceof Boolean && object2 instanceof Boolean) {
			return ((Boolean) object1).compareTo((Boolean) object2);
		}

		Double number1 = toDouble(object1);
		Double number2 = toDouble(object2);
		return number1.compareTo(number2);
	}
}
