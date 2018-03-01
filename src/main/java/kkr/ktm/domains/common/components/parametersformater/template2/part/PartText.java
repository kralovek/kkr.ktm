package kkr.ktm.domains.common.components.parametersformater.template2.part;

import kkr.ktm.domains.common.components.parametersformater.template2.Position;

public class PartText extends PartBase implements Part {

	private String text;

	public PartText(Position position, String text) {
		super(position);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return text;
	}
}
