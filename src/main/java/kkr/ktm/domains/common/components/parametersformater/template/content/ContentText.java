package kkr.ktm.domains.common.components.parametersformater.template.content;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.parametersformater.template.Position;

public class ContentText extends ContentBase implements Content {
	private static final Logger LOG = Logger.getLogger(ContentText.class);
	private String text = "";

	public ContentText(Position position) {
		super(position);
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void setText(String text) {
		this.text = text != null ? text : "";
	}

	public String evaluate(ContextContent context) {
		return text;
	}

	public String toString() {
		return text;
	}
}
