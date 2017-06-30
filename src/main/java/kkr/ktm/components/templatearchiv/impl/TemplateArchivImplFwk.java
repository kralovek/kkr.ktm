package kkr.ktm.components.templatearchiv.impl;


import java.io.File;
import java.util.List;
import java.util.Map;

import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;


/**
 * TemplateArchivImplFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class TemplateArchivImplFwk {
    private boolean configured;

	protected List<File> dirs;

	protected Map<String, String> mapping;

	public void config() throws BaseException {
		configured = false;
		if (dirs == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter dirs is not configured");
		} else if (dirs.isEmpty()) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": The list of template directories 'dirs' is empty");
		}
		for (File dir : dirs) {
			if (!dir.isDirectory()) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Template archiv directory does not exist: "
						+ dir.getAbsolutePath());
			}
		}
		if (mapping != null) {
			for (final Map.Entry<String, String> entry : mapping.entrySet()) {
				if (entry.getKey().isEmpty() || entry.getValue() == null
						|| entry.getValue().isEmpty())
					throw new ConfigurationException(getClass().getSimpleName()
							+ ": Bad template mapping: " + entry.getKey()
							+ ": " + entry.getValue());
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public List<File> getDirs() {
		return dirs;
	}

	public void setDirs(List<File> dirs) {
		this.dirs = dirs;
	}

	public Map<String, String> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}
}
