package kkr.ktm.domains.common.components.diffmanager.filesystem.local;

import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.domains.common.components.diffmanager.data.DiffIndex;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;

public class DiffItemImpl implements DiffItem {
	private String name;
	private DiffIndex diffIndex;
	private DiffStatus diffStatus;
	private Map<String, String> parameters = new TreeMap<String, String>();

	public DiffItemImpl(DiffItem diffItem) {
		name = diffItem.getName();
		diffIndex = diffItem.getIndex();
		diffStatus = diffItem.getStatus();
		parameters.putAll(diffItem.getParameters());
	}

	public DiffItemImpl(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DiffIndex getIndex() {
		return diffIndex;
	}

	public DiffStatus getStatus() {
		return diffStatus;
	}

	public void setIndex(DiffIndex diffIndex) {
		this.diffIndex = diffIndex;
	}

	public void setStatus(DiffStatus diffStatus) {
		this.diffStatus = diffStatus;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
}
