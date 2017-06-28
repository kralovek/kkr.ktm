package kkr.ktm.domains.common.components.diffmanager.database.comparators;

import java.util.Comparator;

public class ComparatorStringHexa implements Comparator<Object> {

	public int compare(Object object1, Object object2) {
		if (object1 == null && object2 == null) {
			return 0;
		} else if (object1 == null) {
			return -1;
		} else if (object2 == null) {
			return +1;
		} else if (object1 instanceof String
				&& object2 instanceof String) {
			String string1 = (String) object1;
			String string2 = (String) object2;
			if (string1.length() != string2.length()) {
				return new Integer(string1.length()).compareTo(string2.length());
			}
			
			for (int i = 0; i < string1.length(); i++) {
				Character c1 = string1.charAt(i);
				Character c2 = string2.charAt(i);
				if (c1 != c2) {
					if (Character.isDigit(c1) && !Character.isDigit(c2)) {
						return -1; 
					} else
					if (!Character.isDigit(c2) && Character.isDigit(c2)) {
						return +1; 
					} else {
						return c1.compareTo(c2); 
					}
				}
			}
		}
		return 0;
	}
}
