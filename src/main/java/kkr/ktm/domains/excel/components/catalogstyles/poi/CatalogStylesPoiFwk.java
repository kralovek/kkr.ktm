package kkr.ktm.domains.excel.components.catalogstyles.poi;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.domains.excelpoi.style.Style;
import kkr.common.errors.ConfigurationException;

public abstract class CatalogStylesPoiFwk {
	private boolean configured;

	private Collection<Style> _styles;
	protected Map<String, Style> styles;

	public void config() throws ConfigurationException {
		configured = false;
		styles = new LinkedHashMap<String, Style>();
		if (_styles != null) {
			int i = 0;
			for (Style style : _styles) {
				if (styles.containsKey(style.getName())) {
					throw new ConfigurationException("Parameter 'styles[" + i + "]' has bad value. Style is already defined");
				} else {
					styles.put(style.getName(), style);
				}
				i++;
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Collection<Style> getStyles() {
		return _styles;
	}

	public void setStyles(Collection<Style> styles) {
		this._styles = styles;
	}
}
