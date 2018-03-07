package kkr.ktm.domains.common.components.formatter.decimal;

import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;

public class FormatterDecimal extends FormatterDecimalFwk implements Formatter {

	public String format(Object object) throws FormatterException {
		if (object == null) {
			return "";
		}
		if (!(object instanceof Number)) {
			throw new FormatterException(getClass().getSimpleName() + ": Formated object is not a number: ", object);
		}

		java.util.Formatter formatter = new java.util.Formatter(LOCALE);
		Number number = (Number) object;
		formatter.format(pattern, number.doubleValue());
		String retval = formatter.toString();
		formatter.close();
		return retval;
	}
}
