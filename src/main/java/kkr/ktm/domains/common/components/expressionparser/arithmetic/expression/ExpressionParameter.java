package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.context.level.ContextLevel;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionEvaluateException;
import kkr.ktm.utils.parser.Position;

public class ExpressionParameter extends ExpressionBase implements Expression {
	private String name;
	private Expression[] indexExpressions;

	public ExpressionParameter(Position position, String name) {
		super(position);
		this.name = name;
		indexExpressions = new Expression[0];
	}

	public ExpressionParameter(Position position, String name, Expression[] indexes) {
		super(position);
		this.name = name;
		this.indexExpressions = indexes;
	}

	public Object evaluate(Context context) throws ExpressionEvaluateException {
		if (context != null && !(context instanceof ContextLevel)) {
			throw new ExpressionEvaluateException(position, toString(), "Unsupported context. Expect: "
					+ ContextLevel.class.getName() + " Received: " + String.valueOf(context));
		}
		ContextLevel contextLevel = (ContextLevel) context;

		Integer[] indexes = null;
		if (indexExpressions != null && indexExpressions.length != 0) {
			indexes = new Integer[indexExpressions.length];
			int i = 0;
			for (; i < indexExpressions.length; i++) {
				Object object = null;
				try {
					object = indexExpressions[i].evaluate(context);
				} catch (ExpressionEvaluateException ex) {
					throw new ExpressionEvaluateException(position, toString(),
							"Cannot evaluate parameter " + toString() + ". Index " + i
									+ " is not evaluated as an integer: " + indexExpressions[i] + " Problem: "
									+ ex.getMessage(),
							ex);
				}
				Number number;
				if (false //
						|| object == null //
						|| !(object instanceof Number)//
						|| (number = (Number) object).doubleValue() != (double) number.intValue()) {
					throw new ExpressionEvaluateException(position, toString(),
							"Cannot evaluate parameter " + toString() + ". Index " + i
									+ " is not evaluated as an integer: " + String.valueOf(object));
				}
				indexes[i] = number.intValue();
			}
		}

		try {
			Object value = contextLevel.getParameter(name, indexes);
			return value;
		} catch (Exception ex) {
			throw new ExpressionEvaluateException(position, toString(),
					"Cannot evaluate parameter: " + name + toStringIndexes(indexes) + " Problem: " + ex.getMessage(),
					ex);
		}
	}

	private String toStringIndexes(Integer[] indexes) {
		if (indexes != null && indexes.length != 0) {
			StringBuffer buffer = new StringBuffer();
			for (int index : indexes) {
				buffer.append("[").append(index).append("]");
			}
			return buffer.toString();
		} else {
			return "";
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Expression expression : indexExpressions) {
			buffer.append('[').append(expression.toString()).append(']');
		}
		return name + buffer.toString();
	}
}
