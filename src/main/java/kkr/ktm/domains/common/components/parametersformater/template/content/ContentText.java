package kkr.ktm.domains.common.components.parametersformater.template.content;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.utils.parser.Position;

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

	public void validate(Context context) throws ContentParseException {
	}

	public String evaluate(Context context) {
		return text;
	}

	public String toString() {
		return text;
	}
}
