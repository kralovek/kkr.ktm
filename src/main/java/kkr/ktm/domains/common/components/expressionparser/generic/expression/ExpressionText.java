package kkr.ktm.domains.common.components.expressionparser.generic.expression;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.utils.parser.Position;

public class ExpressionText extends ExpressionBase implements Expression {
	private String text;

	public ExpressionText(Position position, String text) {
		super(position);
		this.text = text;
	}

	public String evaluate(Context context) {
		return text;
	}

	public String toString() {
		return text;
	}
}
