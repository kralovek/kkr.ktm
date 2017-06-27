package kkr.ktm.components.lancer.infotel_table;

import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.data.TestOutput;

public class TestOutputImpl implements TestOutput {

	private Map<String, Object> dataOutput = new LinkedHashMap<String, Object>();
	private String source;
	private String type;
	private String id;

	public TestOutputImpl(String source, String type, String id, Map<String, Object> dataOutput) {
		super();
		this.source = source;
		this.type = type;
		this.id = id;
		this.dataOutput.putAll(dataOutput);
	}

	public Map<String, Object> getDataOutput() {
		return dataOutput;
	}

	public String getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}
