package kkr.ktm.domains.common.components.templateloader.file;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsConfig;

public abstract class TemplateLoaderFileFwk {
	private boolean configured;

	protected Collection<File> dirs;

	private Map<String, String> _mapping;
	protected Map<Pattern, String> mapping;

	public void config() throws BaseException {
		configured = false;
		if (dirs == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter dirs is not configured");
		} else if (dirs.isEmpty()) {
			throw new ConfigurationException(getClass().getSimpleName() + ": The list of template directories 'dirs' is empty");
		}
		for (File dir : dirs) {
			if (!dir.isDirectory()) {
				throw new ConfigurationException(getClass().getSimpleName() + ": Template archiv directory does not exist: " + dir.getAbsolutePath());
			}
		}

		mapping = UtilsConfig.checkMapPatterns(_mapping, "mapping");

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Collection<File> getDirs() {
		return dirs;
	}

	public void setDirs(Collection<File> dirs) {
		this.dirs = dirs;
	}

	public Map<String, String> getMapping() {
		return _mapping;
	}

	public void setMapping(Map<String, String> mapping) {
		this._mapping = mapping;
	}
}
