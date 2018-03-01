package kkr.ktm.domains.common.components.parametersformater.template.part;

import kkr.ktm.domains.common.components.parametersformater.template.Position;

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
