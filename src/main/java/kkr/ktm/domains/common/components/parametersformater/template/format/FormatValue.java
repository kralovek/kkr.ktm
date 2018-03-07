package kkr.ktm.domains.common.components.parametersformater.template.format;

import java.util.Date;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

public class FormatValue extends FormatBase implements Format {
	private String format;

	public FormatValue(String format) {
		super(FormatType.VALUE);
		if (format == null) {
			throw new IllegalArgumentException("Format is not defined");
		}
		this.format = format;
	}

	public String format(Object value) {
		if (value == null) {
			return "";
		}
		try {
			if (value instanceof Date) {
				throw new IllegalArgumentException("Date cannot be formated with the value formatter");
			}

			Formatter formatter = new Formatter(Locale.US);
			formatter.format(format, value);
			String retval = formatter.toString();
			formatter.close();
			return retval;
		} catch (IllegalFormatException ex) {
			throw new IllegalArgumentException(
					"Bad format pattern: " + format + " for the type: " + value.getClass().getName(), ex);
		}
	}

	public String toString() {
		return format;
	}
}
