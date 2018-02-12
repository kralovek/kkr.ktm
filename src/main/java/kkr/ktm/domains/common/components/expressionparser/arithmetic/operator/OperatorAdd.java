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
}
