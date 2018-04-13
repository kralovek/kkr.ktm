package kkr.ktm.domains.common.components.expressionparser.generic.level;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.generic.error.ExpressionParseException;
import kkr.ktm.domains.common.components.expressionparser.generic.operator.Operator;
import kkr.ktm.utils.parser.Position;

public interface Level {

	Expression first(Position position, Operator operator, Expression expression) throws ExpressionParseException;

	Operator getOperator();

	Level nextLevel();
}
