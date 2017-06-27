package kkr.ktm.utils.excel;

import java.util.ArrayList;
import java.util.List;

public class Value {

	public static final String FLAG_IORD = "IORD";
	
	private Object value;
	
	private List<String> flags = new ArrayList<String>();
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public List<String> getFlags() {
		return flags;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (String flag : flags) {
			buffer.append("<").append(flag).append(">");
		}
		if (value != null) {
			buffer.append(value.toString());
		} else {
			buffer.append("(null)");
		}
		return buffer.toString();
	}
}
