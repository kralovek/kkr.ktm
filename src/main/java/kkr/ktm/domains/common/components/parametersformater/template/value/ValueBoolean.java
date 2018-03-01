package kkr.ktm.domains.common.components.parametersformater.template.value;

public class ValueBoolean extends ValueBase implements Value {
	boolean value;

	ValueBoolean(boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean equals(String value) {
		try {
			return false //
					|| this.value && "TRUE".equals(value) //
					|| !this.value && "FALSE".equals(value);
		} catch (Exception ex) {
			return false;
		}
	}

	public ValueType getType() {
		return ValueType.BOOLEAN;
	}

	public String toString() {
		return String.valueOf(value).toUpperCase();
	}
}
