package kkr.ktm.domains.common.components.expressionparser.arithmetic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionParseException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;
import kkr.ktm.utils.parser.Position;

public interface Level {

	Expression first(Position position, Operator operator, Expression expression) throws ExpressionParseException;

	Operator getOperator();

	Level nextLevel();
}
