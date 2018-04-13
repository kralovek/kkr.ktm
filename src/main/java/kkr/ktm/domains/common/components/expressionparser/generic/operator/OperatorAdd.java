package kkr.ktm.domains.common.components.expressionparser.generic.operator;

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

	public Number evaluate(Number argument1, Number argument2) {
		switch (this) {
		case PLUS:
			return argument1.doubleValue() + argument2.doubleValue();
		case MINUS:
		default:
			return argument1.doubleValue() - argument2.doubleValue();
		}
	}
}
