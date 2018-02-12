package kkr.ktm.domains.common.components.expressionparser.arithmetic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ParseExpressionException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.OperatorMulti;

public class LevelMulti implements Level {

	private OperatorMulti operatorMulti = OperatorMulti.TIMES;
	private LevelExp nextLevel = new LevelExp();

	public Expression first(Position position, Operator operator, Expression expression)
			throws ParseExpressionException {
		if (operator != null) {
			throw new ParseExpressionException(position,
					"Expression cannot start with the operator " + operator.getSymbol());
		}
		return expression;
	}

	public LevelExp nextLevel() {
		return nextLevel;
	}

	public OperatorMulti getOperator() {
		return operatorMulti;
	}
}
