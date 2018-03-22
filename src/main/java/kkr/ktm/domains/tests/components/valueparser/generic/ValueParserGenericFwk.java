package kkr.ktm.domains.tests.components.valueparser.generic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import kkr.common.errors.ConfigurationException;

public abstract class ValueParserGenericFwk {
	private boolean configured;

	private static final DateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private Collection<String> _dateFormats;
	protected Collection<DateFormat> dateFormats;

	public void config() throws ConfigurationException {
		configured = false;
		dateFormats = new ArrayList<DateFormat>();
		if (_dateFormats == null || _dateFormats.isEmpty()) {
			dateFormats.add(DEFAULT_DATE_TIME_FORMAT);
			dateFormats.add(DEFAULT_DATE_FORMAT);
		} else {
			int i = 0;
			Date date = new Date();
			Set<String> values = new HashSet<String>();
			for (String _dateFormat : _dateFormats) {
				if (!values.add(_dateFormat)) {
					throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'dateFormats[" + i
							+ "]' has duplicated value: " + _dateFormat);
				}
				try {
					DateFormat dateFormat = new SimpleDateFormat(_dateFormat);
					dateFormat.format(date);
					dateFormats.add(dateFormat);
					i++;
				} catch (Exception ex) {
					throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'dateFormats[" + i
							+ "]' is not a valid date format: " + _dateFormat, ex);
				}
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Collection<String> getDateFormats() {
		return _dateFormats;
	}

	public void setDateFormats(Collection<String> dateFormats) {
		this._dateFormats = dateFormats;
	}
}
