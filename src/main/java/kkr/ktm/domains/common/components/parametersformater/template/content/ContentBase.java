package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.utils.parser.Position;

public abstract class ContentBase {

	protected Position position;

	public ContentBase(Position position) {
		super();
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

}
