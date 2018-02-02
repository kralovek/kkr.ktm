package kkr.ktm.domains.common.components.parametersformater.template.format;

import kkr.ktm.domains.common.components.parametersformater.template.value.Value;

public class FormatAuto extends FormatBase implements Format {

	public FormatAuto() {
		super(FormatType.AUTO);
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
