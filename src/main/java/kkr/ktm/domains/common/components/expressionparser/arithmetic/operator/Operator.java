package kkr.ktm.domains.common.components.expressionparser.arithmetic.operator;

public interface Operator {

	Operator valueOfSymbol(char symbol);

	char getSymbol();

	double evaluate(double argument1, double argument2);
}
