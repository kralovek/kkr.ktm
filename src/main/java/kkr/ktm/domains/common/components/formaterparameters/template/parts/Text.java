package kkr.ktm.domains.common.components.formaterparameters.template.parts;

public class Text implements Part {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String pValue) {
        this.value = pValue;
    }
    
    public String toString() {
    	return String.valueOf(value);
    }
}
