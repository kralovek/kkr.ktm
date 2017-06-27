package kkr.ktm.domains.excel.data;

import kkr.ktm.domains.tests.data.Test;

public class TestExcel implements Test {

	protected String source;

	protected String type;

	protected String code;

	protected Integer group;

	protected String name;

	protected String description;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String id) {
		this.code = id;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
