package kkr.ktm.domains.common.components.parametersformater.template.parts;

import kkr.common.utils.UtilsString;

public class TagIf implements Tag, Open {
	public static final String TAG = "IF";
	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_TYPE = "TYPE";
	public static final String ATTR_VALUE = "VALUE";

	public static enum Type {
		EQ, EMPTY, NE, NONEMPTY
	}

	private String name;
	private String[] indexes;
	private Type type;
	private String value;

	public String getTagName() {
		return TAG;
	}

	public String[] getIndexes() {
		return indexes;
	}

	public void setIndexes(String[] indexes) {
		this.indexes = indexes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static String getSyntax() {
		return "[" + TAG //
				+ " " + ATTR_NAME + "=" + "\"ParameterName\"" //
				+ " " + ATTR_INDEXES + "=" + "\"IndexName1,IndexName2,...\"" //
				+ " " + ATTR_TYPE + "=" + UtilsString.arrayToString(Type.values(), "\"", "\"", "|") //
				+ " " + ATTR_VALUE + "=" + "Value" //
				+ "]";
	}
}
