package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;

public class ExpressionNumber implements Expression {
	private double value;

	public ExpressionNumber(double value) {
		super();
		this.value = value;
	}

	public double evaluate(Context context) {
		return value;
	}

	public String toString() {
		return String.valueOf(value);
	}
}
