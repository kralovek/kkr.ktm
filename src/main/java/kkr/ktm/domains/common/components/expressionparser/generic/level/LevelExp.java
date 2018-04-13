package kkr.ktm.domains.common.components.expressionparser.generic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.generic.error.ExpressionParseException;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.Operator;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.OperatorExp;
import kkr.ktm.utils.parser.Position;

public class LevelExp implements Level {

	private OperatorExp operatorExp = OperatorExp.EXPONENT;

	public Expression first(Position position, Operator operator, Expression expression)
			throws ExpressionParseException {
		if (operator != null) {
			throw new ExpressionParseException(position, "Expression cannot start with this operator",
					String.valueOf(operator.getSymbol()));
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
