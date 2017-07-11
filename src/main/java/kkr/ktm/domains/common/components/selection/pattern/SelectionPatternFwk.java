package kkr.ktm.domains.common.components.selection.pattern;

import java.util.Collection;
import java.util.regex.Pattern;

import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsConfig;

public abstract class SelectionPatternFwk {
	private boolean configured;

	private Collection<String> _patterns;
	protected Collection<Pattern> patterns;
	private Collection<String> _exceptPatterns;
	protected Collection<Pattern> exceptPatterns;
	private String _mode;
	protected AcceptMode mode;

	public void config() throws ConfigurationException {
		configured = false;

		patterns = UtilsConfig.checkCollectionPatterns(_patterns, "patterns");
		exceptPatterns = UtilsConfig.checkCollectionPatterns(_exceptPatterns, "exceptPatterns");
		if (_mode == null) {
			mode = AcceptMode.INCLUDE;
		} else {
			try {
				mode = AcceptMode.valueOf(_mode);
			} catch (IllegalArgumentException ex) {
				throw new ConfigurationException("Parameter 'mode' has bad value: " + _mode + ". Allowed values are: " + AcceptMode.INCLUDE.toString()
						+ "," + AcceptMode.EXCLUDE.toString());
			}
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Collection<String> getPatterns() {
		return _patterns;
	}

	public void setPatterns(Collection<String> masks) {
		this._patterns = masks;
	}

	public Collection<String> getExceptPatterns() {
		return _exceptPatterns;
	}

	public void setExceptPatterns(Collection<String> exceptMasks) {
		this._exceptPatterns = exceptMasks;
	}

	public String getMode() {
		return _mode;
	}

	public void setMode(String mode) {
		_mode = mode;
	}
}
