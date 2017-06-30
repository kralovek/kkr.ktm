package kkr.ktm.domains.common.components.selection.valuelist;

import java.util.Collection;
import java.util.LinkedHashSet;

import kkr.common.errors.ConfigurationException;

public abstract class SelectionValueListFwk {
	private boolean configured;

	private static final String SEPARATOR = "|";

	protected String separator;

	private String _values;
	protected Collection<String> values;

	public void config() throws ConfigurationException {
		configured = false;
		if (separator == null) {
			separator = SEPARATOR;
		}
		if (_values == null) {
			throw new ConfigurationException("Parameter 'values' is not configured");
		} else {
			String[] tokens = _values.split(separator);
			values = new LinkedHashSet<String>();
			for (String token : tokens) {
				values.add(token.trim());
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getValues() {
		return _values;
	}

	public void setValues(String values) {
		this._values = values;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
