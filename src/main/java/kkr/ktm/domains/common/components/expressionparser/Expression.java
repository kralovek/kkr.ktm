package kkr.ktm.domains.common.components.expressionparser;

import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionEvaluateException;

public interface Expression {
	Number evaluate(ContextExpression context) throws ExpressionEvaluateException;
}
