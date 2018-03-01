package kkr.ktm.domains.common.components.parametersformater.template.value;

import java.util.Date;

public class ValueDate extends ValueBase implements Value {
	Date value;

	ValueDate(Date value) {
		if (value == null) {
			throw new IllegalArgumentException("Null value is not allowed in ValueDate constructor");
		}
		this.value = value;
	}

	public Date getValue() {
		return value;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean equals(String value) {
		throw new IllegalStateException("ValueDate cannot be compared to a string");
	}

	public ValueType getType() {
		return ValueType.DATE;
	}

	public String toString() {
		return value.toString();
	}
}
