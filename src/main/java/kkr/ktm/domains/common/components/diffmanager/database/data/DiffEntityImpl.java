package kkr.ktm.domains.common.components.diffmanager.database.data;

import java.util.ArrayList;
import java.util.Collection;

import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.common.components.diffmanager.data.DiffIndex;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;

public class DiffEntityImpl implements DiffEntity, Comparable<DiffEntity> {
	private String name;
	private DiffIndex lastIndex;
	private Collection<DiffItem> diffItems = new ArrayList<DiffItem>();

	public DiffEntityImpl(String name) {
		this.name = name;
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

	public Collection<DiffItem> getItems() {
		return diffItems;
	}

	public int compareTo(DiffEntity diffEntity) {
		return name.compareTo(diffEntity.getName());
	}

	public String toString() {
		return "[NAME: " + name + "] [INDEX: " + lastIndex + "] [ITEMS: " + diffItems.size() + "]";
	}
}
