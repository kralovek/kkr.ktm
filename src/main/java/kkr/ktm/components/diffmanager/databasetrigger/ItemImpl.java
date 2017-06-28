package kkr.ktm.components.diffmanager.databasetrigger;

import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;

public class ItemImpl implements DiffItem, Comparable<DiffItem> {
	private String name;
	private IndexImpl index;
	private DiffStatus diffStatus;
	private Map<String, String> parameters = new TreeMap<String, String>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public IndexImpl getIndex() {
		return index;
	}

	public DiffStatus getStatus() {
		return diffStatus;
	}

	public void setIndex(IndexImpl index) {
		this.index = index;
	}

	public void setStatus(DiffStatus diffStatus) {
		this.diffStatus = diffStatus;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public int compareTo(DiffItem diffItem) {
		return name.compareTo(diffItem.getName());
	}
}
