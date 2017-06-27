package kkr.ktm.components.lancer.m2o_importfile;

import java.util.HashMap;
import java.util.Map;

import kkr.ktm.data.TestOutput;

public class TestOutputImpl implements TestOutput {

	private Map<String, Object> dataOutput = new HashMap<String, Object>();
	private String source;
	private String type;
	private String id;

	public TestOutputImpl(String source, String type, String id) {
		super();
		this.source = source;
		this.type = type;
		this.id = id;
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
