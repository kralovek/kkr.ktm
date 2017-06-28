package kkr.ktm.domains.common.components.diffmanager.databasetrigger;

import java.util.ArrayList;
import java.util.List;

import kkr.ktm.domains.common.components.diffmanager.data.DiffGroup;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;

public class DiffGroupImpl implements DiffGroup, Comparable<DiffGroup> {
	private String name;
	private DiffIndexImpl lastIndex;
	private List<DiffItem> diffItems = new ArrayList<DiffItem>();

	public DiffGroupImpl(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DiffIndexImpl getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(DiffIndexImpl lastIndex) {
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
