package kkr.ktm.components.diffmanager.filesystem.local;

import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.components.diffmanager.data.DiffIndex;
import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;

public class ItemImpl implements DiffItem {
	private String name;
	private DiffIndex diffIndex;
	private DiffStatus diffStatus;
	private Map<String, String> parameters = new TreeMap<String, String>();

	public ItemImpl(DiffItem diffItem) {
		name = diffItem.getName();
		diffIndex = diffItem.getIndex();
		diffStatus = diffItem.getStatus();
		parameters.putAll(diffItem.getParameters());
	}

	public ItemImpl(String name) {
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
