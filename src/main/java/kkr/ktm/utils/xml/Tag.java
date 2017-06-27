package kkr.ktm.utils.xml;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private String prefix;

    private String name;

    private String value;

    private List<Attribute> attributes = new ArrayList<Attribute>();

    private List<Tag> tags = new ArrayList<Tag>();

    public String getComplexName() {
        return prefix != null && !"".equals(prefix) ? prefix + ":" + name : name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String pPrefix) {
        this.prefix = pPrefix;
    }

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        this.name = pName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String pValue) {
        this.value = pValue;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
