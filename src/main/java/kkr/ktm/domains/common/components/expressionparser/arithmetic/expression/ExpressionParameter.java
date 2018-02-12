package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;

public class ExpressionParameter implements Expression {
	private String name;

	public ExpressionParameter(String name) {
		super();
		this.name = name;
	}

	public double evaluate(Context context) {
		double value = context.getParameter(name);
		return value;
	}

	public String toString() {
		return "{" + name + "}";
	}
}
