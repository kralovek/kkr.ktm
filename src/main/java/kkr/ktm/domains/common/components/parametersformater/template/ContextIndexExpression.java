package kkr.ktm.domains.common.components.parametersformater.template;

import java.util.Map;

import kkr.ktm.domains.common.components.expressionparser.ContextExpression;

public class ContextIndexExpression implements ContextExpression {
	private static final String INDEX = "THIS";
	private Number valueIndex;
	private Map<String, Number> parameters;

	public ContextIndexExpression(Map<String, Number> parameters) {
		this.parameters = parameters;
	}

	public void setValueIndex(Number valueIndex) {
		this.valueIndex = valueIndex;
	}

	public Number getParameter(String name, Integer... indexes) {
		if (INDEX.equals(name)) {
			return valueIndex;
		}
		return parameters.get(name);
	}
}
