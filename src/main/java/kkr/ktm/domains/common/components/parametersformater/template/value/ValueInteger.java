package kkr.ktm.domains.common.components.parametersformater.template.value;

public class ValueInteger extends ValueBase implements Value {
	private long value;

	public ValueInteger(int value) {
		this.value = (long) value;
	}

	public ValueInteger(long value) {
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean equals(String value) {
		try {
			int valueInt = Integer.parseInt(value);
			return this.value == valueInt;
		} catch (Exception ex) {
			return false;
		}
	}

	public ValueType getType() {
		return ValueType.INTEGER;
	}

	public String toString() {
		return String.valueOf(value);
	}
}
