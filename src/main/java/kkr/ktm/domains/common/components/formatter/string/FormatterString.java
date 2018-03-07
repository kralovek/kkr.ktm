package kkr.ktm.domains.common.components.formatter.string;

import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;

public class FormatterString extends FormatterStringFwk implements Formatter {

	public String format(Object object) throws FormatterException {
		if (object == null) {
			return "";
		}
		String string = String.valueOf(object);
		java.util.Formatter formatter = new java.util.Formatter(LOCALE);
		formatter.format(pattern, string);
		String retval = formatter.toString();
		formatter.close();
		return retval;
	}
}
