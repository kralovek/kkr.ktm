package kkr.ktm.domains.common.components.parametersformater.template.parts;

/**
 * TagLoopBegin
 *
 * @author KRALOVEC-99999
 */
public class TagLoop implements Part, Open {
    public static final String TAG = "LOOP";
    public static final String ATTR_INDEX = "INDEX";
    public static final String ATTR_INDEXES = "INDEXES";
    public static final String ATTR_NAME = "NAME";
    public static final String ATTR_TYPE = "TYPE";

    public static final String TYPE_COUNT = "COUNT";
    public static final String TYPE_LENGTH = "LENGTH";

    public static enum Type {
    	COUNT, LENGTH
    };
    
    private String index;
    private String[] indexes;
    private String name;
    private Type type;

    public String getTagName() {
        return TAG;
    }

    public String getIndex() {
		return index;
	}


	public void setIndex(String index) {
		this.index = index;
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


	public static String getSyntax() {
        return "[" + TAG + " " + ATTR_INDEX + "=\"IndexName\"" + " " + ATTR_NAME + "=\"ParameterName\"" + " " + ATTR_INDEXES + "=\"IndexNameList\"" + ATTR_TYPE + "=\""
				+ TYPE_COUNT + "|" + TYPE_LENGTH + "\"]";
    }

    public String toString() {
    	return "[" + TAG
    			+ (index != null ? " " + ATTR_INDEX + "=\"" + index + "\"" : "")
    			+ (type != null ? " " + ATTR_TYPE + "=\"" + type + "\"" : "")
    			+ (name != null ? " " + ATTR_NAME + "=\"" + name + "\"" : "")
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
