package kkr.ktm.components.executant.xstream.data;

import java.util.List;

public class InputParameters {
    private String instanceName;
    private String methodname;
    private List<Object> parameters;

    public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
}
