package kkr.ktm.components.diffmanager.database.comparators;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.exception.ConfigurationException;

public abstract class ComparatorLinesFwk {

	private boolean configured;
	
	protected Map<String, Comparator<Object>> comparators;

	public void config() throws ConfigurationException {
		configured = false;
		if (comparators == null) {
			comparators = new LinkedHashMap<String, Comparator<Object>>();
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public Map<String, Comparator<Object>> getComparators() {
		return comparators;
	}

	public void setComparators(Map<String, Comparator<Object>> comparators) {
		this.comparators = comparators;
	}
}
