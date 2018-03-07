package kkr.ktm.domains.common.components.formatter.integer;

import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;

public class FormatterInteger extends FormatterIntegerFwk implements Formatter {

	public String format(Object object) throws FormatterException {
		if (object == null) {
			return "";
		}
		if (!(object instanceof Number) || ((Number) object).doubleValue() != (double) ((Number) object).intValue()) {
			throw new FormatterException(getClass().getSimpleName() + ": Formated object is not an integer number: ",
					object);
		}

		java.util.Formatter formatter = new java.util.Formatter(LOCALE);
		formatter.format(pattern, object);
		String retval = formatter.toString();
		formatter.close();
		return retval;
	}
}
