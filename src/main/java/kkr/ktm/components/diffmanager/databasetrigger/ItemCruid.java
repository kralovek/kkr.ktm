package kkr.ktm.components.diffmanager.databasetrigger;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;

public class ItemCruid {
	private String name;
	private IndexImpl index;
	private DiffStatus diffStatus;
	private Map<String, Object> parameters = new TreeMap<String, Object>();

	private DateFormat patternDate;

	public ItemCruid(DateFormat patternDate) {
		super();
		this.patternDate = patternDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public IndexImpl getIndex() {
		return index;
	}

	public DiffStatus getStatus() {
		return diffStatus;
	}

	public void setIndex(IndexImpl index) {
		this.index = index;
	}

	public void setStatus(DiffStatus diffStatus) {
		this.diffStatus = diffStatus;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public DiffItem toItem() {
		ItemImpl itemImpl = new ItemImpl();

		itemImpl.setIndex(index);
		itemImpl.setName(name);
		itemImpl.setStatus(diffStatus);

		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			String value;
			if (entry.getValue() == null) {
				value = "";
			} else if (entry.getValue() instanceof Date) {
				value = entry.getValue() != null ? patternDate.format((Date) entry.getValue()) : "";
			} else if (entry.getValue() instanceof Double) {
				value = toString((Double) entry.getValue());
			} else {
				value = String.valueOf(entry.getValue());
			}
			itemImpl.getParameters().put(entry.getKey(), value);
		}

		return itemImpl;
	}

	private static String toString(double value) {
		String strValue = String.valueOf(value);
		int iPos = strValue.lastIndexOf('.');
		if (iPos != -1) {
			int count0 = 0;
			for (int i = strValue.length() - 1; i >= iPos; i--) {
				if (strValue.charAt(i) == '0') {
					count0++;
				} else if (strValue.charAt(i) == '.') {
					count0++;
				} else {
					break;
				}
			}
			if (count0 != 0) {
				return strValue.substring(0, strValue.length() - count0);
			}
		}
		return strValue;
	}

	public String toString() {
		return "" + index + "-" + name + "-" + diffStatus;
	}
}
