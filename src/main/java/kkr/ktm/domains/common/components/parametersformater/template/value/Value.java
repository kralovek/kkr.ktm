package kkr.ktm.domains.common.components.parametersformater.template.value;

public interface Value {
	Object getValue();

	boolean isEmpty();

	boolean equals(String value);
}
