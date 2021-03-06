package kkr.ktm.domains.common.components.expressionparser.generic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.generic.error.ExpressionParseException;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.Operator;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.OperatorMulti;
import kkr.ktm.utils.parser.Position;

public class LevelMulti implements Level {

	private OperatorMulti operatorMulti = OperatorMulti.TIMES;
	private LevelExp nextLevel = new LevelExp();

	public Expression first(Position position, Operator operator, Expression expression)
			throws ExpressionParseException {
		if (operator != null) {
			throw new ExpressionParseException(position, "Expression cannot start with this operator",
					String.valueOf(operator.getSymbol()));
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
