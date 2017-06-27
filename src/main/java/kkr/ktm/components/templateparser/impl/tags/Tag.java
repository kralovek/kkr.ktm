package kkr.ktm.components.templateparser.impl.tags;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tag
 *
 * @author KRALOVEC-99999
 */
public class Tag {
    private String name;
    private Map<String, String> attributes = new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        this.name = pName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public String toString() {
    	StringBuffer buffer = new StringBuffer("[" + name);
    	for (Map.Entry<String, String> entry : attributes.entrySet()) {
        	buffer.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
    	}
    	buffer.append("]");
    	return buffer.toString();
    }
}
