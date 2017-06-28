package kkr.ktm.domains.common.components.parametersformater.template.content;

public class Index implements Comparable<Index> {
    private String name;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Index pIndex) {
        return name.compareTo(pIndex.getName());
    }
}
