package kkr.ktm.domains.common.components.expressionparser;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.generic.error.ExpressionEvaluateException;

public interface Expression {
	Object evaluate(Context context) throws ExpressionEvaluateException;
}
