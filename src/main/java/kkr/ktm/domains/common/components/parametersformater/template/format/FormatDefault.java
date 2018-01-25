package kkr.ktm.domains.common.components.parametersformater.template.format;

import kkr.ktm.domains.common.components.parametersformater.template.value.Value;

public class FormatDefault extends FormatBase implements Format {

	public FormatDefault() {
		super(FormatType.DEFAULT);
	}

	public String format(Value value) {
		if (value == null) {
			return "";
		}
		return value.getValue().toString();
	}

	public String toString() {
		return "";
	}
}
