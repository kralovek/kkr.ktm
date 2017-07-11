package kkr.ktm.domains.common.components.diffmanager.filesystem.data;

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

	public Collection<DiffItem> getItems() {
		return diffItems;
	}

	public String getName() {
		return name;
	}

	public DiffIndex getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(DiffIndexImpl lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int compareTo(DiffEntity diffEntity) {
		return name.compareTo(diffEntity.getName());
	}

	public String toString() {
		return "[NAME: " + name + "] [INDEX: " + lastIndex + "] [ITEMS: " + diffItems.size() + "]";
	}
}
