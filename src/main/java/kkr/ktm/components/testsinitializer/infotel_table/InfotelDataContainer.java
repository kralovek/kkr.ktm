package kkr.ktm.components.testsinitializer.infotel_table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class InfotelDataContainer {
	private Map<String, Collection<Map<String, Object>>> container = new LinkedHashMap<String, Collection<Map<String, Object>>>();

	public void putTypeData(String type, Collection<Map<String, Object>> data) {
		container.put(type, data);
	}

	public Collection<Map<String, Object>> getTypeData(String type) {
		Collection<Map<String, Object>> retval = container.get(type);
		if (retval == null) {
			retval = new ArrayList<Map<String, Object>>();
			container.put(type, retval);
		}
		return retval;
	}
}
