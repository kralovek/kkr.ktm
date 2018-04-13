package kkr.ktm.domains.common.components.expressionparser.generic.operator;

public enum OperatorSeparator implements Operator {
	COMMA(',');

	private char symbol;

	private OperatorSeparator(char symbol) {
		this.symbol = symbol;
	}

	public OperatorSeparator valueOfSymbol(char symbol) {
		for (OperatorSeparator operator : values()) {
			if (operator.symbol == symbol) {
				return operator;
			}
		}
		return null;
	}

	public char getSymbol() {
		return symbol;
	}

	public OperatorSeparator getType() {
		return COMMA;
	}

	public Number evaluate(Number argument1, Number argument2) {
		throw new IllegalStateException("Not implemented");
	}
}
