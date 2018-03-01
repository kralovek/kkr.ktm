package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.ContextExpression;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionEvaluateException;

public class ExpressionParameter implements Expression {
	private String name;
	private Expression[] indexExpressions;

	public ExpressionParameter(String name) {
		super();
		this.name = name;
		indexExpressions = new Expression[0];
	}

	public ExpressionParameter(String name, Expression[] indexes) {
		super();
		this.name = name;
		this.indexExpressions = indexes;
	}

	public Number evaluate(ContextExpression context) throws ExpressionEvaluateException {
		Integer[] indexes = null;
		if (indexExpressions != null && indexExpressions.length != 0) {
			indexes = new Integer[indexExpressions.length];
			for (int i = 0; i < indexExpressions.length; i++) {
				Number number = indexExpressions[i].evaluate(context);
				if (number.longValue() != (long) number.doubleValue()) {
					throw new ExpressionEvaluateException(
							"Evaluated index of parameter " + name + " is not an integer: " + number);
				}
				indexes[i] = number.intValue();
			}
			/*
			 * for (int i = 0; i < indexExpressions.length; i++) { if (value == null) {
			 * throw new EvaluateExpressionException( "Value of parameter " + name +
			 * toStringIndexes(indexes) + " is null"); } if (!value.getClass().isArray()) {
			 * throw new EvaluateExpressionException("Value of parameter " + name +
			 * toStringIndexes(indexes) + " has lower dimmension than required"); } Object[]
			 * values = (Object[]) value; if (indexes[i] >= values.length) { throw new
			 * EvaluateExpressionException( "Value of parameter " + name +
			 * toStringIndexes(indexes) + " is out of bound"); } value = values[indexes[i]];
			 * }
			 * 
			 * if (value.getClass().isArray()) { throw new
			 * EvaluateExpressionException("Value of parameter " + name +
			 * toStringIndexes(indexes) + " has higher dimmension than required"); }
			 */
		}

		Object value = context.getParameter(name, indexes);
		if (value == null) {
			throw new ExpressionEvaluateException("Unknown parameter: " + name);
		}

		if (!(value instanceof Number)) {
			throw new ExpressionEvaluateException(
					"Value of parameter " + name + toStringIndexes(indexes) + " is not a number: " + value.toString());
		}

		return (Number) value;
	}

	private String toStringIndexes(Integer[] indexes) {
		if (indexes != null && indexes.length != 0) {
			StringBuffer buffer = new StringBuffer();
			for (int index : indexes) {
				buffer.append("[").append(index).append("]");
			}
			return buffer.toString();
		} else {
			return "0";
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Expression expression : indexExpressions) {
			buffer.append('[').append(expression.toString()).append(']');
		}
		return "{" + name + buffer.toString() + "}";
	}
}
