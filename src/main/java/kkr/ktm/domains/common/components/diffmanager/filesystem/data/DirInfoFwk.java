package kkr.ktm.domains.common.components.diffmanager.filesystem.data;

import java.util.regex.Pattern;

import kkr.common.errors.ConfigurationException;

public class DirInfoFwk {
	private boolean configured;

	protected String name;
	protected String path;
	private String _pattern;
	protected Pattern pattern;
	protected boolean content = false;

	public void config() throws ConfigurationException {
		configured = false;
		if (name == null) {
			throw new ConfigurationException("Parameter 'name' is not configured");
		}
		if (path == null) {
			throw new ConfigurationException("Parameter 'path' is not configured");
		}

		if (_pattern == null) {
			pattern = null;
		} else {
			try {
				pattern = Pattern.compile(_pattern);
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'pattern' has bad value");
			}
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getMask() {
		return _pattern;
	}

	public void setMask(String pattern) {
		this._pattern = pattern;
	}

	public boolean isContent() {
		return content;
	}

	public void setContent(boolean content) {
		this.content = content;
	}
}
