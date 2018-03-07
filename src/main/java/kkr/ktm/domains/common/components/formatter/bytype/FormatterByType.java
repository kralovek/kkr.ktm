package kkr.ktm.domains.common.components.formatter.bytype;

import java.util.Date;

import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;

public class FormatterByType extends FormatterByTypeFwk implements Formatter {

	public String format(Object object) throws FormatterException {
		if (object == null) {
			return "";
		} else //
		if (object instanceof Date) {
			return formatterDate.format(object);
		} else //
		if (object instanceof Boolean) {
			return formatterBoolean.format(object);
		} else //
		if (object instanceof String) {
			return formatterString.format(object);
		} else //
		if (false //
				|| object instanceof Short //
				|| object instanceof Integer //
				|| object instanceof Long) {
			return formatterInteger.format(object);
		} else //
		if (object instanceof Number) {
			return formatterDecimal.format(object);
		} else {
			return formatterAuto.format(object);
		}
	}
}
