package kkr.ktm.domains.orchestrator.data;

import java.util.HashMap;
import java.util.Map;

import kkr.ktm.domains.tests.data.TestOutput;
import kkr.common.utils.UtilsString;

public class TestOutputImpl implements TestOutput {
	private String name;
	private String description;

	private String source;
	private String type;
	private String code;
	private Integer group;

	private Map<String, Object> dataOutput = new HashMap<String, Object>();

	public TestOutputImpl(String name, String description, String source, String type, String code, Integer group) {
		super();
		if (UtilsString.isEmpty(source)) {
			throw new IllegalArgumentException("Source is empty");
		}
		if (UtilsString.isEmpty(type)) {
			throw new IllegalArgumentException("Type is empty");
		}
		if (UtilsString.isEmpty(code)) {
			throw new IllegalArgumentException("Code is empty");
		}

		this.name = name;
		this.description = description;
		this.source = source;
		this.type = type;
		this.code = code;
		this.group = group;
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

	public String getCode() {
		return code;
	}

	public Integer getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

}
