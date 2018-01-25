package kkr.ktm.domains.common.components.parametersformater.template.parts;

import kkr.ktm.domains.common.components.parametersformater.template.format.Format;

public class TagIndex implements Part {
	public static final String TAG = "INDEX";
	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_FORMAT = "FORMAT";

	protected String name;
	protected Format format;

	public String getTagName() {
		return TAG;
	}

	public String getName() {
		return name;
	}

	public void setName(final String pName) {
		this.name = pName;
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
				+ "]";
	}
}
