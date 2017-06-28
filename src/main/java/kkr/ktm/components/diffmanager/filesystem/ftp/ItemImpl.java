package kkr.ktm.components.diffmanager.filesystem.ftp;

import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;

public class ItemImpl implements DiffItem, Cloneable {
	private String name;
	private IndexImpl index;
	private DiffStatus diffStatus;
	private Map<String, String> parameters = new TreeMap<String, String>();

	public ItemImpl(DiffItem diffItem) {
		name = diffItem.getName();
		index = (IndexImpl) diffItem.getIndex();
		diffStatus = diffItem.getStatus();
		parameters.putAll(diffItem.getParameters());
	}

	public ItemImpl(String name) {
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

	public ItemImpl clone() {
		ItemImpl item = new ItemImpl(name);
		item.index = index;
		item.diffStatus = diffStatus;
		item.getParameters().putAll(parameters);
		return item;
	}
}
