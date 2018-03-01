package kkr.ktm.domains.common.components.parametersformater.template.value;

public class ValueDecimal extends ValueBase implements Value {
	double value;

	public ValueDecimal(double value) {
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean equals(String value) {
		try {
			double valueDouble = Double.parseDouble(value);
			return this.value == valueDouble;
		} catch (Exception ex) {
			return false;
		}
	}

	public ValueType getType() {
		return ValueType.DECIMAL;
	}

	public String toString() {
		return String.valueOf(value);
	}
}
