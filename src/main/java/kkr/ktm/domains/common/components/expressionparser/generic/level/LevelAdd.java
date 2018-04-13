package kkr.ktm.domains.common.components.expressionparser.generic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.generic.error.ExpressionParseException;
import kkr.ktm.domains.common.components.expressionparser.generic.expression.ExpressionNumber;
import kkr.ktm.domains.common.components.expressionparser.generic.expression.ExpressionOperator;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.Operator;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.OperatorAdd;
import kkr.ktm.utils.parser.Position;

public class LevelAdd implements Level {

	private OperatorAdd operatorAdd = OperatorAdd.PLUS;
	private LevelMulti nextLevel = new LevelMulti();

	public Expression first(Position position, Operator operator, Expression expression)
			throws ExpressionParseException {
		if (operator == OperatorAdd.MINUS) {
			ExpressionNumber expressionNumber = new ExpressionNumber(position, 0);
			ExpressionOperator expressionOperator = new ExpressionOperator(position, operator, expressionNumber,
					expression);
			return expressionOperator;
		}
		return expression;
	}

	public LevelMulti nextLevel() {
		return nextLevel;
	}

	public OperatorAdd getOperator() {
		return operatorAdd;
	}
}
