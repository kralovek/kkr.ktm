package kkr.ktm.domains.common.components.diffmanager.filesystem.local;

import java.util.ArrayList;
import java.util.List;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DirInfo;
import kkr.ktm.utils.UtilsKtm;

public abstract class DiffManagerFilesystemLocalFwk {
	private boolean configured;

	protected String code;
	protected List<DirInfo> dirInfos;

	public void config() throws ConfigurationException {
		configured = false;
		if (dirInfos == null) {
			dirInfos = new ArrayList<DirInfo>();
		} else {
			for (DirInfo dirInfo : dirInfos) {
				if (dirInfo.getPath() == null) {
					throw new ConfigurationException(
							getClass().getSimpleName() + "." + dirInfo.getClass().getSimpleName() + ": Parameter dir is not configured");
				}
				if (dirInfo.getName() == null) {
					dirInfo.setName(dirInfo.getPath());
				}
			}
		}

		code = UtilsKtm.checkEntityName(code, "code");

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public List<DirInfo> getDirInfos() {
		return dirInfos;
	}

	public void setDirInfos(List<DirInfo> dirInfos) {
		this.dirInfos = dirInfos;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
