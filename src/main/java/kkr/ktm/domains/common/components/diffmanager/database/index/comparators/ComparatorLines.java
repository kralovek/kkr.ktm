package kkr.ktm.domains.common.components.diffmanager.database.index.comparators;

import java.util.Comparator;
import java.util.Map;

import kkr.ktm.domains.common.components.diffmanager.database.data.ItemCruid;

public class ComparatorLines extends ComparatorLinesFwk implements Comparator<ItemCruid> {

	public int compare(ItemCruid item1, ItemCruid item2) {
		testConfigured();
		
		for (Map.Entry<String, Comparator<Object>> entry : comparators.entrySet()) {
			Comparator<Object> comparator = entry.getValue();
			
			Object value1 = item1.getParameters().get(entry.getKey());
			Object value2 = item2.getParameters().get(entry.getKey());
			
			int result = 0;
			if (value1 == null && value2 == null) {
				return 0;
			} else if (value1 == null) {
				return -1;
			} else if (value2 == null) {
				return +1;
			} else {
				result = comparator.compare(value1,  value2);
				if (result != 0) {
					return result;
				}
			}
		}
		return 0;
	}
}
