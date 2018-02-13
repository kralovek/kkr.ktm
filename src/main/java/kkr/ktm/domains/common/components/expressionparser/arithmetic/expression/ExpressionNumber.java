package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;

public class ExpressionNumber implements Expression {
	private Number value;

	public ExpressionNumber(Number value) {
		super();
		if (value == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}
		this.value = value;
	}

	public Number evaluate(Context context) {
		return value;
	}

	public String toString() {
		return String.valueOf(value);
	}
}
