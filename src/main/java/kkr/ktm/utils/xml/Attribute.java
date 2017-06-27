package kkr.ktm.utils.xml;

public class Attribute {
    private String prefix;

    private String name;

    private String value;

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
}
