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
}
