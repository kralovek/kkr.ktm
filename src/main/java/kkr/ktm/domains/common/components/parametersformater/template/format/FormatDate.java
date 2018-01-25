package kkr.ktm.domains.common.components.parametersformater.template.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueDate;

public class FormatDate extends FormatBase implements Format {
	private DateFormat format;

	public FormatDate(String format) {
		super(FormatType.DATE);
		if (format == null) {
			throw new IllegalArgumentException("Format is not defined");
		}
		try {
			this.format = new SimpleDateFormat(format);
			this.format.format(new Date());
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unsupported date format: " + format, ex);
		}
	}

	public String format(Value value) {
		if (value == null) {
			return "";
		}
		if (!(value instanceof ValueDate)) {
			throw new IllegalArgumentException("Formated object is not a Date");
		}
		return format.format(((ValueDate) value).getValue());
	}

	public String toString() {
		return format.toString();
	}
}
