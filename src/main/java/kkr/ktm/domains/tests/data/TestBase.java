package kkr.ktm.domains.tests.data;

import kkr.common.utils.UtilsString;

public abstract class TestBase implements Test {

	protected String source;

	protected String type;

	protected String code;

	protected Integer group;

	protected String name;

	protected String description;

	public TestBase(Test test) {
		this(test.getName(), test.getDescription(), test.getSource(), test.getType(), test.getCode(), test.getGroup());
	}

	public TestBase(String name, String description, String source, String type, String code, Integer group) {
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

	public String toString() {
		return "[CODE: " + code + "]" + " [TYPE: " + type + "]" + " [SOURCE: " + source + "]" + (group != null ? " [GROUP: " + group + "]" : "");
	}
}
