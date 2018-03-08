package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;

public abstract class ExpressionBase {

	protected Position position;

	public ExpressionBase(Position position) {
		this.position = position;
	}
}
