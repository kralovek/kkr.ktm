package kkr.ktm.domains.common.components.parametersformater.template2.part;

import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.domains.common.components.parametersformater.template2.Position;

public class PartTag extends PartBase implements Part {
	private String name;
	private Map<String, String> attributes = new LinkedHashMap<String, String>();

	public PartTag(Position position, String name) {
		super(position);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addAttribute(String name, String value) {
		attributes.put(name, value);
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
