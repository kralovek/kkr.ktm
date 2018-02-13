package kkr.ktm.domains.common.components.expressionparser.arithmetic.operator;

public enum OperatorAdd implements Operator {
	PLUS('+'), MINUS('-');

	private char symbol;

	private OperatorAdd(char symbol) {
		this.symbol = symbol;
	}

	public OperatorAdd valueOfSymbol(char symbol) {
		for (OperatorAdd operator : values()) {
			if (operator.symbol == symbol) {
				return operator;
			}
		}
		return null;
	}

	public char getSymbol() {
		return symbol;
	}

	public OperatorAdd getType() {
		return PLUS;
	}

	public double evaluate(double argument1, double argument2) {
		switch (this) {
		case PLUS:
			return argument1 + argument2;
		case MINUS:
		default:
			return argument1 - argument2;
		}
	}
}
