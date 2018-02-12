package kkr.ktm.domains.common.components.expressionparser.arithmetic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.Position;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ParseExpressionException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;

public interface Level {

	Expression first(Position position, Operator operator, Expression expression) throws ParseExpressionException;

	Operator getOperator();

	Level nextLevel();
}
