package kkr.ktm.components.templateparser.impl.parts;

public class TagIndex implements Part {
    public static final String TAG = "INDEX";
    public static final String ATTR_NAME = "NAME";
    public static final String ATTR_FORMAT = "FORMAT";

    protected String name;
    protected String format;

    public String getTagName() {
        return TAG;
    }

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        this.name = pName;
    }

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String toString() {
    	return "[" + TAG
    			+ (name != null ? " " + ATTR_NAME + "=\"" + name + "\"" : "")
    			+ (format != null ? " " + ATTR_FORMAT + "=\"" + format + "\"" : "") + "]";
    }
}
