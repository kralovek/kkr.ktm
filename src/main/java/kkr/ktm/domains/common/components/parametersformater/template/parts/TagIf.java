package kkr.ktm.domains.common.components.parametersformater.template.parts;

public class TagIf implements Part, Open {
	public static final String TAG = "IF";
	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_TYPE = "TYPE";
	public static final String ATTR_VALID = "VALID";
	public static final String ATTR_VALUE = "VALUE";

	public static final String TYPE_NONEMPTY = "NONEMPTY";
	public static final String TYPE_NE = "NE";
	public static final String TYPE_EMPTY = "EMPTY";
	public static final String TYPE_EQ = "EQ";

	public static enum Type {
		EQ, EMPTY, NE, NONEMPTY
	}

	private String name;
	private String[] indexes;
	private Type type;
	private Boolean valid;
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

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static String getSyntax() {
		return "[" + TAG + " " + ATTR_NAME + "=\"ParameterName\"" + " "
				+ ATTR_INDEXES + "=\"IndexNameList\"" + " " + ATTR_TYPE + "=\""
				+ TYPE_EMPTY + "|" + TYPE_NONEMPTY + "|" + TYPE_EQ + "|" + TYPE_NE + "\"" + " " + ATTR_VALID
				+ "=\"TRUE|FALSE\"" + "]";
	}
}
