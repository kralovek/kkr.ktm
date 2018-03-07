package kkr.ktm.domains.common.components.context.index;

import java.util.HashMap;
import java.util.Map;

public class ContextIndex {

	private Map<String, Integer> indexes = new HashMap<String, Integer>();

	public int getIndex(String name) {
		Integer index = indexes.get(name);
		if (index == null) {
			throw new IllegalArgumentException("Index does not exist in the context: " + name);
		}
		return index;
	}

	public void setIndexes(Map<String, Integer> indexes) {
		this.indexes.clear();
		if (indexes != null) {
			this.indexes.putAll(indexes);
		}
	}

	public void addIndex(String name, int value) {
		if (indexes.containsKey(name)) {
			throw new IllegalArgumentException("Index already exists in the context: " + name);
		}
		indexes.put(name, value);
	}

	public void updateIndex(String name, int value) {
		if (!indexes.containsKey(name)) {
			throw new IllegalArgumentException("Index does not exist in the context: " + name);
		}
		indexes.put(name, value);
	}

	public void removeIndex(String name) {
		if (!indexes.containsKey(name)) {
			throw new IllegalArgumentException("Index does not exist in the context: " + name);
		}
		indexes.remove(name);
	}

	public Integer[] evaluateIndexes(String... indexNames) {
		if (indexNames == null || indexNames.length == 0) {
			return new Integer[0];
		}
		Integer[] indexValues = new Integer[indexNames.length];
		for (int i = 0; i < indexNames.length; i++) {
			Integer value = indexes.get(indexNames[i]);
			if (value == null) {
				throw new IllegalArgumentException("Unknown index name: " + indexNames[i]);
			}
			indexValues[i] = value;
		}
		return indexValues;
	}
}
