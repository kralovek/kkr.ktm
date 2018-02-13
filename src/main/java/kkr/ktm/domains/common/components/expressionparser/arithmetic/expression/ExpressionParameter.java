package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.EvaluateExpressionException;

public class ExpressionParameter implements Expression {
	private String name;

	public ExpressionParameter(String name) {
		super();
		this.name = name;
	}

	public double evaluate(Context context) throws EvaluateExpressionException {
		Double value = context.getParameter(name);
		if (value == null) {
			throw new EvaluateExpressionException("Unknown parameter: " + name);
		}
		return value;
	}

	public String toString() {
		return "{" + name + "}";
	}
}
