package kkr.ktm.domains.common.components.formatter.auto;

import kkr.ktm.domains.common.components.formatter.Formatter;

public class FormatterAuto extends FormatterAutoFwk implements Formatter {

	public String format(Object object) {
		if (object == null) {
			return "";
		}
		return String.valueOf(object);
	}
}
