package kkr.ktm.domains.common.components.expressionparser.arithmetic.operator;

public interface Operator {

	Operator valueOfSymbol(char symbol);

	char getSymbol();
}
