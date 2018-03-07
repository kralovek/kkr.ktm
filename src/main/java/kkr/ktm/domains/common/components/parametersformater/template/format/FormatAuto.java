package kkr.ktm.domains.common.components.parametersformater.template.format;

import kkr.ktm.domains.common.components.parametersformater.template.value.UtilsValue;

public class FormatAuto extends FormatBase implements Format {

	public FormatAuto() {
		super(FormatType.AUTO);
	}

	public String format(Object value) {
		if (value == null) {
			return "";
		}
		if (UtilsValue.isValidValue(value)) {
			throw new IllegalArgumentException("Formated object is not a supported value: " + value);
		}
		return value.toString();
	}

	public String toString() {
		return "";
	}
}
