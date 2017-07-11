package kkr.ktm.domains.common.components.diffmanager.filesystem.data;

import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.domains.common.components.diffmanager.data.DiffIndex;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;

public class DiffItemImpl implements DiffItem, Cloneable {
	private String name;
	private DiffIndex index;
	private DiffStatus diffStatus;
	private Map<String, Object> parameters = new TreeMap<String, Object>();

	public DiffItemImpl(DiffItem diffItem) {
		name = diffItem.getName();
		index = (DiffIndexImpl) diffItem.getIndex();
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
		return index;
	}

	public DiffStatus getStatus() {
		return diffStatus;
	}

	public void setIndex(DiffIndexImpl index) {
		this.index = index;
	}

	public void setStatus(DiffStatus diffStatus) {
		this.diffStatus = diffStatus;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public DiffItemImpl clone() {
		DiffItemImpl item = new DiffItemImpl(name);
		item.index = index;
		item.diffStatus = diffStatus;
		item.getParameters().putAll(parameters);
		return item;
	}
}
