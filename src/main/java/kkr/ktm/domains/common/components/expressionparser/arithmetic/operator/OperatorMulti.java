package kkr.ktm.domains.common.components.expressionparser.arithmetic.operator;

public enum OperatorMulti implements Operator {
	TIMES('*'), DEVIDED('/');

	private char symbol;

	private OperatorMulti(char symbol) {
		this.symbol = symbol;
	}

	public OperatorMulti valueOfSymbol(char symbol) {
		for (OperatorMulti operator : values()) {
			if (operator.symbol == symbol) {
				return operator;
			}
		}
		return null;
	}

	public char getSymbol() {
		return symbol;
	}

	public OperatorMulti getType() {
		return TIMES;
	}

	public double evaluate(double argument1, double argument2) {
		switch (this) {
		case TIMES:
			return argument1 * argument2;
		case DEVIDED:
		default:
			if (argument2 == 0) {
				throw new IllegalArgumentException("Division by 0");
			}
			return argument1 / argument2;
		}
	}
}
