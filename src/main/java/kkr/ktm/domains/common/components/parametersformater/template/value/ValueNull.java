package kkr.ktm.domains.common.components.parametersformater.template.value;

public class ValueNull extends ValueBase implements Value {

	public String getValue() {
		return "";
	}

	public boolean isEmpty() {
		return true;
	}

	public boolean equals(String value) {
		return value == null || value.isEmpty();
	}

	public String toString() {
		return "";
	}
}
