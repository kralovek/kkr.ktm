package kkr.ktm.data;

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
}
