package kkr.ktm.domains.common.components.parametersformater.template.part;

import kkr.ktm.domains.common.components.parametersformater.template.Position;

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
