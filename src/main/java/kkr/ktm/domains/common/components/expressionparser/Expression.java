package kkr.ktm.domains.common.components.expressionparser;

import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.EvaluateExpressionException;

public interface Expression {
	Number evaluate(Context context) throws EvaluateExpressionException;
}
