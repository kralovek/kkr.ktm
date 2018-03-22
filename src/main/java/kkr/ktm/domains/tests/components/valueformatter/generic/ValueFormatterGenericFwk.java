package kkr.ktm.domains.tests.components.valueformatter.generic;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsString;

public abstract class ValueFormatterGenericFwk {
	private boolean configured;

	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

	private static final char DEFAULT_DECIMAL_POINT = '.';
	private static final Character[] ALLOWED_DECIMAL_POINT = { '.', ',' };

	private static final boolean DEFAULT_SYNTAX_FORMATTING = false;

	private String decimalPattern;
	private Character decimalPoint;
	protected DecimalFormat numberFormat;
	protected Boolean syntaxFormatting;

	private String _dateTimeFormat;
	protected DateFormat dateTimeFormat;

	private String _dateFormat;
	protected DateFormat dateFormat;

	public void config() throws ConfigurationException {
		configured = false;
		if (_dateTimeFormat == null) {
			_dateTimeFormat = DEFAULT_DATE_FORMAT;
		}
		try {
			dateTimeFormat = new SimpleDateFormat(_dateTimeFormat);
			dateTimeFormat.format(new Date());
		} catch (Exception ex) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'dateTimeFormat' is not a valid date format");
		}

		if (_dateFormat == null) {
			dateFormat = dateTimeFormat;
		} else {
			try {
				dateFormat = new SimpleDateFormat(_dateFormat);
				dateFormat.format(new Date());
			} catch (Exception ex) {
				throw new ConfigurationException(
						getClass().getSimpleName() + ": Parameter 'dateFormat' is not a valid date format");
			}
		}

		if (decimalPoint == null) {
			decimalPoint = DEFAULT_DECIMAL_POINT;
		} else {
			boolean found = false;
			for (int i = 0; i < ALLOWED_DECIMAL_POINT.length; i++) {
				if (decimalPoint.equals(ALLOWED_DECIMAL_POINT[i])) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new ConfigurationException(
						getClass().getSimpleName() + ": Parameter 'decimalPoint' has bad value. Allowed values are: "
								+ UtilsString.toStringArray(ALLOWED_DECIMAL_POINT, null, null, ","));
			}
		}
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		decimalFormatSymbols.setDecimalSeparator(decimalPoint);
		if (decimalPattern != null) {
			numberFormat = new DecimalFormat(decimalPattern);
		} else {
			numberFormat = new DecimalFormat();
		}
		numberFormat.setGroupingUsed(false);
		numberFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		if (syntaxFormatting == null) {
			syntaxFormatting = DEFAULT_SYNTAX_FORMATTING;
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getDateTimeFormat() {
		return _dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this._dateTimeFormat = dateTimeFormat;
	}

	public String getDateFormat() {
		return _dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this._dateFormat = dateFormat;
	}

	public Character getDecimalPoint() {
		return decimalPoint;
	}

	public void setDecimalPoint(Character decimalPoint) {
		this.decimalPoint = decimalPoint;
	}

	public Boolean getSyntaxFormatting() {
		return syntaxFormatting;
	}

	public void setSyntaxFormatting(Boolean syntaxFormatting) {
		this.syntaxFormatting = syntaxFormatting;
	}

	public String getDecimalPattern() {
		return decimalPattern;
	}

	public void setDecimalPattern(String decimalPattern) {
		this.decimalPattern = decimalPattern;
	}
}
