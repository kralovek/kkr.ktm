package kkr.ktm.components.diffmanager.databasetrigger;

import java.util.ArrayList;
import java.util.List;

import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.components.diffmanager.data.DiffItem;

public class GroupImpl implements DiffGroup, Comparable<DiffGroup> {
	private String name;
	private IndexImpl lastIndex;
	private List<DiffItem> diffItems = new ArrayList<DiffItem>();

	public GroupImpl(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public IndexImpl getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(IndexImpl lastIndex) {
		this.lastIndex = lastIndex;
	}

	public List<DiffItem> getItems() {
		return diffItems;
	}

	public int compareTo(DiffGroup diffGroup) {
		return name.compareTo(diffGroup.getName());
	}

	public String toString() {
		return name;
	}
}
