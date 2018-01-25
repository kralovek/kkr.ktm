package kkr.ktm.domains.common.components.parametersformater.template.value;

public class ValueText extends ValueBase implements Value {
	private String value;

	ValueText(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Null value is not allowed in the ValueText constructor");
		}
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean isEmpty() {
		return value.isEmpty();
	}

	public boolean equals(String value) {
		return this.value.equals(value);
	}

	public String toString() {
		return value;
	}
}
