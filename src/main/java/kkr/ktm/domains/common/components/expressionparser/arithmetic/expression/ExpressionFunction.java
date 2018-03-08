package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.calculator.Calculator;
import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionEvaluateException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.OperatorSeparator;

public class ExpressionFunction extends ExpressionBase implements Expression {
	private String name;
	private String function;
	private Expression[] argumentExpressions;
	private Calculator calculator;

	public ExpressionFunction(Position position, String name, Expression[] argumentExpressions, Calculator calculator)
			throws ExpressionEvaluateException {
		super(position);
		this.name = name;
		this.calculator = calculator;
		this.argumentExpressions = argumentExpressions;
		this.function = name;
	}

	public Object evaluate(Context context) throws ExpressionEvaluateException {
		try {
			Object[] argumentValues = new Object[argumentExpressions.length];
			for (int i = 0; i < argumentValues.length; i++) {
				argumentValues[i] = argumentExpressions[i].evaluate(context);
				if (argumentValues[i] == null) {
					throw new ExpressionEvaluateException(position, toString(),
							"Cannot call function " + toString() + ". Problem: argument[" + i + "] is null");
				}
			}
			Object value = calculator.calculate(function, argumentValues);
			if (value == null) {
				throw new ExpressionEvaluateException(position, toString(),
						"Cannot call function " + toString() + ". Problem: result is null");
			}
			return value;
		} catch (ExpressionEvaluateException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ExpressionEvaluateException(position, toString(),
					"Cannot call function " + toString() + ". Problem: " + ex.getMessage(), ex);
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Expression argumentExpression : argumentExpressions) {
			if (buffer.length() != 0) {
				buffer.append(OperatorSeparator.COMMA.getSymbol());
			}
			buffer.append(argumentExpression.toString());
		}
		return name + "(" + buffer.toString() + ")";
	}
}
