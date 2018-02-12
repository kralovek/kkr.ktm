package kkr.ktm.domains.common.components.expressionparser;

import kkr.common.errors.BaseException;

public interface ExpressionParser {

	Expression parseExpression(String text) throws BaseException;
}
