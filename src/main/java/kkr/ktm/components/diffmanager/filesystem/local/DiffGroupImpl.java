package kkr.ktm.components.diffmanager.filesystem.local;

import java.util.ArrayList;
import java.util.List;

import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.components.diffmanager.data.DiffIndex;
import kkr.ktm.components.diffmanager.data.DiffItem;

public class DiffGroupImpl implements DiffGroup, Comparable<DiffGroup> {
	private String name;
	private DiffIndex lastIndex;
	private List<DiffItem> diffItems = new ArrayList<DiffItem>();

	public DiffGroupImpl(String name) {
		this.name = name;
	}

	public List<DiffItem> getItems() {
		return diffItems;
	}

	public String getName() {
		return name;
	}

	public DiffIndex getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(DiffIndex lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int compareTo(DiffGroup diffGroup) {
		return name.compareTo(diffGroup.getName());
	}
}
