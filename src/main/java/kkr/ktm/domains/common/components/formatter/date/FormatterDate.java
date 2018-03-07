package kkr.ktm.domains.common.components.formatter.date;

import java.util.Date;

import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;

public class FormatterDate extends FormatterDateFwk implements Formatter {

	public String format(Object object) throws FormatterException {
		if (object == null) {
			return "";
		}
		if (!(object instanceof Date)) {
			throw new FormatterException(getClass().getName() + ": Formatted object is not a date", object);
		}
		return dateFormat.format((Date) object);
	}
}
