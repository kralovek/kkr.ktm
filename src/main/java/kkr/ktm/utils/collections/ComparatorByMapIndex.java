package kkr.ktm.utils.collections;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ComparatorByMapIndex<K extends Comparable> implements Comparator<K> {
	Map<K, Long> index = new HashMap<K, Long>();

	public ComparatorByMapIndex() {
	}

	public int compare(K value1, K value2) {
		Long index1 = index.get(value1);
		Long index2 = index.get(value2);
		Integer result = compareNull(index1, index2);
		if (result != null) {
			return result;
		}
		result = compareNull(value2, value2);
		if (result != null) {
			return result;
		}
		return 0;
	}

	private Integer compareNull(Comparable comparable1, Comparable comparable2) {
		if (comparable1 != null && comparable2 != null) {
			return comparable1.compareTo(comparable2);
		} else if (comparable1 == null && comparable2 == null) {
			return null;
		} else {
			return comparable1 != null ? -1 : 1;
		}
	}

	public Map<K, Long> getIndex() {
		return index;
	}
}
