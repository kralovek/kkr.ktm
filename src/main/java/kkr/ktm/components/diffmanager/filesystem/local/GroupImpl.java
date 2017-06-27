package kkr.ktm.components.diffmanager.filesystem.local;

import java.util.ArrayList;
import java.util.List;

import kkr.ktm.components.diffmanager.DiffManager.Group;
import kkr.ktm.components.diffmanager.DiffManager.Index;
import kkr.ktm.components.diffmanager.DiffManager.Item;

public class GroupImpl implements Group, Comparable<Group> {
	private String name;
	private Index lastIndex;
	private List<Item> items = new ArrayList<Item>();

	public GroupImpl(String name) {
		this.name = name;
	}

	public List<Item> getItems() {
		return items;
	}

	public String getName() {
		return name;
	}

	public Index getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(Index lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int compareTo(Group group) {
		return name.compareTo(group.getName());
	}
}
