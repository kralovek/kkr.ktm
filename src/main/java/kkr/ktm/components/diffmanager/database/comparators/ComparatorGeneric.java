package kkr.ktm.components.diffmanager.database.comparators;

import java.util.Comparator;

public class ComparatorGeneric implements Comparator<Object> {

	public int compare(Object object1, Object object2) {
		if (object1 == null && object2 == null) {
			return 0;
		} else if (object1 == null) {
			return -1;
		} else if (object2 == null) {
			return +1;
		} else if (object1 instanceof Comparable
				&& object2 instanceof Comparable) {
			return ((Comparable) object1).compareTo(object2);
		} else {
			return object1.toString().compareTo(object2.toString());
		}
	}
}
