package kkr.ktm.domains.common.components.parametersformater.template.tags;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tag {
	private String name;
	private Map<String, String> attributes = new LinkedHashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(final String pName) {
		this.name = pName;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("[" + name);
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			buffer.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
		}
		buffer.append("]");
		return buffer.toString();
	}
}
