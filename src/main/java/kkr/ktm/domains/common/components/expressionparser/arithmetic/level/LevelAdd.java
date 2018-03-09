package kkr.ktm.domains.common.components.expressionparser.arithmetic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionParseException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.expression.ExpressionNumber;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.expression.ExpressionOperator;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.OperatorAdd;
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
