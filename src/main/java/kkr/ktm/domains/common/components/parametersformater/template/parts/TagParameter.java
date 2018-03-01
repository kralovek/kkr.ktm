package kkr.ktm.domains.common.components.parametersformater.template.parts;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.parametersformater.template.format.Format;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatType;

public class TagParameter implements TagBase {
	public static final String TAG = "PARAMETER";
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_FORMAT = "FORMAT";
	public static final String ATTR_FORMAT_TYPE = "FORMAT-TYPE";

	protected String name;
	protected Format format;
	private String[] indexes;

	public String getTagName() {
		return TAG;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getIndexes() {
		return indexes;
	}

	public void setIndexes(String[] indexes) {
		this.indexes = indexes;
	}

	public boolean isIndexed() {
		return indexes != null && indexes.length != 0;
	}

	public static String getSyntax() {
		return "[" + TAG //
				+ " " + ATTR_NAME + "=\"IndexName\"" //
				+ " " + ATTR_INDEXES + "=" + "\"IndexName1,IndexName2,...\"" //
				+ " " + ATTR_FORMAT + "=" + "\"Pattern\"" //
				+ " " + ATTR_FORMAT_TYPE + "=" + UtilsString.arrayToString(FormatType.values(), "\"", "\"", "|") //
				+ "]";
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String toString() {
		return "[" + TAG //
				+ (name != null ? " " + ATTR_NAME + "=\"" + name + "\"" : "") //
				+ (format != null ? " " + ATTR_FORMAT + "=\"" + format + "\"" : "") //
				+ (format != null ? " " + ATTR_FORMAT_TYPE + "=\"" + format.getType() + "\"" : "") //
				+ (indexes != null ? " " + ATTR_INDEXES + "=\"" + toStringIndexes(indexes) + "\"" : "") //
				+ "]";
	}

	private String toStringIndexes(String[] indexes) {
		StringBuffer buffer = new StringBuffer();
		for (String index : indexes) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}
			buffer.append(index);
		}
		return buffer.toString();
	}
}
