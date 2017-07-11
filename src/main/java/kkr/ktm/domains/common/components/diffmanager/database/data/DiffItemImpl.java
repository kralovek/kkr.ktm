package kkr.ktm.domains.common.components.diffmanager.database.data;

import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.domains.common.components.diffmanager.data.DiffIndex;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;

public class DiffItemImpl implements DiffItem, Comparable<DiffItem> {
	private String name;
	private DiffIndex index;
	private DiffStatus diffStatus;
	private Map<String, Object> parameters = new TreeMap<String, Object>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DiffIndex getIndex() {
		return index;
	}

	public DiffStatus getStatus() {
		return diffStatus;
	}

	public void setIndex(DiffIndex index) {
		this.index = index;
	}

	public void setStatus(DiffStatus diffStatus) {
		this.diffStatus = diffStatus;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public int compareTo(DiffItem diffItem) {
		return name.compareTo(diffItem.getName());
	}

	public String toString() {
		return "[NAME: " + name + "] [INDEX: " + index.toString() + "] [STATUS: " + diffStatus + "]";
	}
}
