package kkr.ktm.components.templateparser.impl.parts;

/**
 * TagParameter
 *
 * @author KRALOVEC-99999
 */
public class TagParameter implements Part {
    public static final String TAG = "PARAMETER";
    public static final String ATTR_INDEXES = "INDEXES";
    public static final String ATTR_NAME = "NAME";
    public static final String ATTR_FORMAT = "FORMAT";

    protected String name;
    protected String format;
    private String[] indexes;

    public String getTagName() {
        return TAG;
    }

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        this.name = pName;
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
        return "[" + TAG + "]";
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
    			+ (format != null ? " " + ATTR_FORMAT + "=\"" + format + "\"" : "")
    			+ (indexes != null ? " " + ATTR_INDEXES + "=\"" + toStringIndexes(indexes) + "\"" : "") + "]";
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
