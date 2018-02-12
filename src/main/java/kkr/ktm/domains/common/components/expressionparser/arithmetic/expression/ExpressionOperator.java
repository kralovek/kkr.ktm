package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;

public class ExpressionOperator implements Expression {
	private Operator operator;
	private Expression expression1;
	private Expression expression2;

	public ExpressionOperator(Operator operator, Expression expression1, Expression expression2) {
		this.operator = operator;
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	public double evaluate(Context context) {
		return 0;
	}

	public String toString() {
		return "(" + expression1.toString() + operator.getSymbol() + expression2.toString() + ")";
	}
}
