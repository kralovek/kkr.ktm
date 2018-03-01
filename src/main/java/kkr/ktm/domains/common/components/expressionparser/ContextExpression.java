package kkr.ktm.domains.common.components.expressionparser;

public interface ContextExpression {
	Number getParameter(String name, Integer... indexes) throws IndexOutOfBoundsException;
}
