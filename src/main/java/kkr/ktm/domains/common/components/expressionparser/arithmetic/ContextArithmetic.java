package kkr.ktm.domains.common.components.expressionparser.arithmetic;

import java.util.Map;

import kkr.ktm.domains.common.components.expressionparser.Context;

public class ContextArithmetic implements Context {

	private Map<String, Number> parameters;

	public ContextArithmetic(Map<String, Number> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String name, Number value) {
		parameters.put(name, value);
	}

	public Number getParameter(String name) {
		return parameters.get(name);
	}
}
