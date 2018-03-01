package kkr.ktm.domains.common.components.parametersformater.template2.part;

import kkr.ktm.domains.common.components.parametersformater.template2.Position;

public abstract class PartBase {
	private Position position;

	public PartBase(Position position) {
		super();
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}
}
