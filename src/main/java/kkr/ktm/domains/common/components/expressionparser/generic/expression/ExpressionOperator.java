package kkr.ktm.domains.common.components.expressionparser.generic.expression;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.generic.error.ExpressionEvaluateException;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.Operator;
import kkr.ktm.utils.parser.Position;

public class ExpressionOperator extends ExpressionBase implements Expression {
	private Operator operator;
	private Expression expression1;
	private Expression expression2;

	public ExpressionOperator(Position position, Operator operator, Expression expression1, Expression expression2) {
		super(position);
		this.operator = operator;
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	public Number evaluate(Context context) throws ExpressionEvaluateException {
		Object object1 = expression1.evaluate(context);
		Object object2 = expression2.evaluate(context);

		Number argument1;
		if (object1 != null && object1 instanceof Number) {
			argument1 = (Number) expression1.evaluate(context);
		} else {
			throw new ExpressionEvaluateException(position, toString(), "Cannot call function " + toString()
					+ ". Problem: argument 1 does not result to a number: " + expression1.toString());
		}
		Number argument2;
		if (object2 != null && object2 instanceof Number) {
			argument2 = (Number) expression2.evaluate(context);
		} else {
			throw new ExpressionEvaluateException(position, toString(), "Cannot call function " + toString()
					+ ". Problem: argument 2 does not result to a number: " + expression1.toString());
		}
		Number value = operator.evaluate(argument1, argument2);
		return value;
	}

	public String toString() {
		return "(" + expression1.toString() + operator.getSymbol() + expression2.toString() + ")";
	}
}
