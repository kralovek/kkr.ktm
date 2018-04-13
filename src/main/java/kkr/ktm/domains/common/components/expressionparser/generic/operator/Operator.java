package kkr.ktm.domains.common.components.expressionparser.generic.operator;

public interface Operator {

	Operator valueOfSymbol(char symbol);

	char getSymbol();

	Number evaluate(Number argument1, Number argument2);
}
