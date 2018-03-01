package kkr.ktm.domains.common.components.parametersformater.template.format;

import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueDate;

public class FormatValue extends FormatBase implements Format {
	private String format;

	public FormatValue(String format) {
		super(FormatType.VALUE);
		if (format == null) {
			throw new IllegalArgumentException("Format is not defined");
		}
		this.format = format;
	}

	public String format(Value value) {
		if (value == null) {
			return "";
		}
		try {
			if (value instanceof ValueDate) {
				throw new IllegalArgumentException("Date cannot be formated with the value formatter");
			}

			Formatter formatter = new Formatter(Locale.US);
			formatter.format(format, value.getValue());
			String retval = formatter.toString();
			formatter.close();
			return retval;
		} catch (IllegalFormatException ex) {
			throw new IllegalArgumentException(
					"Bad format pattern: " + format + " for the type: " + value.getValue().getClass().getName(), ex);
		}
	}

	public String toString() {
		return format;
	}
}
