package kkr.ktm.domains.common.components.expressionparser.arithmetic;

public class Position {
	private String expression;
	private int position;

	public Position(String expression) {
		this.expression = expression;
		this.position = 0;
	}

	public Position(String expression, int position) {
		this.expression = expression;
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public Position movePosition(int move) {
		return new Position(expression, position + move);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("[" + String.valueOf(position) + "]");
		if (expression != null) {
			buffer.append("\n" + expression + "\n");
			for (int i = 0; i < expression.length(); i++) {
				if (i != position) {
					buffer.append('_');
				} else {
					buffer.append('^');
				}
			}
			buffer.append('\n');
		}
		return buffer.toString();
	}
}
