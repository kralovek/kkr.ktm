package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;

public class ExpressionFunction implements Expression {
	private String name;
	private Expression expression;

	public ExpressionFunction(String name, Expression expression) {
		this.name = name;
		this.expression = expression;
	}

	public double evaluate(Context context) {
		return 0;
	}

	public String toString() {
		return name + "(" + expression.toString() + ")";
	}
}
