package kkr.ktm.domains.common.components.parametersformater.template.value;

import java.util.Date;

public abstract class ValueBase {

	public static Value newValue(Object value) {
		Object decomposedValue = value;
		if (decomposedValue != null && value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			if (array.length == 1) {
				decomposedValue = array[0];
			} else if (array.length == 0) {
				decomposedValue = null;
			} else {
				throw new IllegalArgumentException("Expected scalar, received array[" + array.length + "]");
			}
		}

		if (decomposedValue == null) {
			return new ValueNull();
		}

		if (decomposedValue.getClass().isArray()) {
			Object[] array = (Object[]) decomposedValue;
			throw new IllegalArgumentException("Expected scalar, received matrix[1," + array.length + "]");
		}

		if (decomposedValue instanceof String) {
			if (((String) decomposedValue).isEmpty()) {
				return new ValueNull();
			}
			return new ValueText((String) decomposedValue);
		}
		if (decomposedValue instanceof Double) {
			return new ValueDecimal((Double) decomposedValue);
		}
		if (decomposedValue instanceof Integer) {
			return new ValueInteger((Integer) decomposedValue);
		}
		if (decomposedValue instanceof Long) {
			return new ValueInteger((Long) decomposedValue);
		}
		if (decomposedValue instanceof Boolean) {
			return new ValueBoolean((Boolean) decomposedValue);
		}
		if (decomposedValue instanceof Date) {
			return new ValueDate((Date) decomposedValue);
		}
		throw new IllegalArgumentException("Unsupported datatype: " + value.getClass().getName());
	}
}
