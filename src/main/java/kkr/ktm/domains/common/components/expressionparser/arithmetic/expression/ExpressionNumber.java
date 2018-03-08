package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;

public class ExpressionNumber extends ExpressionBase implements Expression {
	private Number value;

	public ExpressionNumber(Position position, Number value) {
		super(position);
		if (value == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}
		this.value = value;
	}

	public Number evaluate(Context context) {
		return value;
	}

	public String toString() {
		String text = String.valueOf(value);
		int iPos = text.indexOf('.');
		if (iPos != -1) {
			text = text.replaceAll("0*$", "");
			if (iPos == text.length() - 1) {
				text = text.substring(0, iPos);
				if (text.length() == 0 || !Character.isDigit(text.charAt(text.length() - 1))) {
					text = "0";
				}
			}
		}
		return text;
	}
}
