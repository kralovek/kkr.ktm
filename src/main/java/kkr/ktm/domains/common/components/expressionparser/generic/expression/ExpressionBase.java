package kkr.ktm.domains.common.components.expressionparser.generic.expression;

import kkr.ktm.utils.parser.Position;

public abstract class ExpressionBase {

	protected Position position;

	public ExpressionBase(Position position) {
		this.position = position;
	}
}
