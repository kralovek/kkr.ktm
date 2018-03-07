package kkr.ktm.domains.common.components.parametersformater.template.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDate extends FormatBase implements Format {
	private DateFormat format;
	private String pattern;

	public FormatDate(String format) {
		super(FormatType.DATE);
		if (format == null) {
			throw new IllegalArgumentException("Format is not defined");
		}
		try {
			this.pattern = format;
			this.format = new SimpleDateFormat(format);
			this.format.format(new Date());
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unsupported date format: " + format, ex);
		}
	}

	public String format(Object value) {
		if (value == null) {
			return "";
		}
		if (!(value instanceof Date)) {
			throw new IllegalArgumentException("Formated object is not a Date: " + value);
		}
		return format.format((Date) value);
	}

	public String toString() {
		return pattern;
	}
}
