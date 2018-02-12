package kkr.ktm.domains.common.components.expressionparser.arithmetic;

import java.util.Map;

import kkr.ktm.domains.common.components.expressionparser.Context;

public class ContextArithmetic implements Context {

	private Map<String, Double> parameters;

	public ContextArithmetic(Map<String, Double> parameters) {
		this.parameters = parameters;
	}

	public Double getParameter(String name) {
		return parameters.get(name);
	}
}
