package kkr.ktm.domains.common.components.expressionparser.arithmetic.operator;

public enum OperatorExp implements Operator {
	EXPONENT('^');

	private char symbol;

	private OperatorExp(char symbol) {
		this.symbol = symbol;
	}

	public OperatorExp valueOfSymbol(char symbol) {
		for (OperatorExp operator : values()) {
			if (operator.symbol == symbol) {
				return operator;
			}
		}
		return null;
	}

	public char getSymbol() {
		return symbol;
	}

	public OperatorExp getType() {
		return EXPONENT;
	}

	public double evaluate(double argument1, double argument2) {
		return Math.pow(argument1, argument2);
	}
}
