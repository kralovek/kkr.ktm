package kkr.ktm.domains.common.components.parametersformater.template;

public class Position {
	private int position;

	public Position() {
		this.position = 0;
	}

	public Position(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public Position movePosition(int move) {
		return new Position(position + move);
	}

	public String toString() {
		return String.valueOf(position);
	}
}
