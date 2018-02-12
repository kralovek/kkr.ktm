package kkr.ktm.domains.common.components.expressionparser.arithmetic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ParseExpressionException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.OperatorExp;

public class LevelExp implements Level {

	private OperatorExp operatorExp = OperatorExp.EXPONENT;

	public Expression first(Position position, Operator operator, Expression expression)
			throws ParseExpressionException {
		if (operator != null) {
			throw new ParseExpressionException(position,
					"Expression cannot start with the operator " + operator.getSymbol());
		}
		return expression;
	}

	public Level nextLevel() {
		return null;
	}

	public OperatorExp getOperator() {
		return operatorExp;
	}
}
