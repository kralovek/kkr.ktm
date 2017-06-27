package kkr.ktm.utils.collections;

import java.util.Map;

public class OrderFiFoMap<K extends Comparable, V> extends java.util.TreeMap<K, V> {

	private Map<K, Long> orders;
	private long index = 0L;
	
	public OrderFiFoMap() {
		super(new ComparatorByMapIndex<K>());
		ComparatorByMapIndex<K> comparator = (ComparatorByMapIndex<K>) this.comparator();
		orders = comparator.getIndex();
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public V put(K key, V value) {
		orders.put(key, index++);
		return super.put(key, value);
	}

	public V remove(Object key) {
		orders.remove(key);
		return super.remove(key);
	}
}
