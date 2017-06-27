package kkr.ktm.components.cleanmanager.filesystem.local;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kkr.ktm.components.cleanmanager.filesystem.DirInfo;
import kkr.ktm.exception.ConfigurationException;


public abstract class CleanManagerFilesystemFwk {
	protected static final String PATH_SEPARATOR = "/";

	private boolean configured;

	protected String name;
	protected List<DirInfo> dirInfos;

	public void config() throws ConfigurationException {
		configured = false;
		if (dirInfos == null) {
			dirInfos = new ArrayList<DirInfo>();
		} else {
			Set<String> names = new HashSet<String>();
			for (DirInfo dirInfo : dirInfos) {
				if (dirInfo.getPath() == null) {
					throw new ConfigurationException(getClass().getSimpleName()
							+ "." + dirInfo.getClass().getSimpleName()
							+ ": Parameter dirPath is not configured");
				}
				if (!dirInfo.getPath().endsWith(PATH_SEPARATOR)) {
					dirInfo.setPath(dirInfo.getPath() + PATH_SEPARATOR);
				}
				if (dirInfo.getName() == null) {
					dirInfo.setName(dirInfo.getPath());
				}
				if (names.contains(dirInfo.getName())) {
					throw new ConfigurationException(getClass().getSimpleName()
							+ "." + dirInfo.getClass().getSimpleName()
							+ ": List dirPath contains more elements with the same name: " + dirInfo.getName());
				}
			}
		}
		if (name == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter name is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DirInfo> getDirInfos() {
		return dirInfos;
	}

	public void setDirInfos(List<DirInfo> dirInfos) {
		this.dirInfos = dirInfos;
	}
}
