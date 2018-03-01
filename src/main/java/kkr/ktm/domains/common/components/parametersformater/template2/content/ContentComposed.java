package kkr.ktm.domains.common.components.parametersformater.template2.content;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.parametersformater.template2.Position;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentEvaluateException;

public class ContentComposed extends ContentBase implements Content {
	private static final Logger LOG = Logger.getLogger(ContentComposed.class);
	private Collection<Content> contents = new ArrayList<Content>();

	public ContentComposed(Position position) {
		super(position);
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void addContent(Content content) {
		contents.add(content);
	}

	public String evaluate(ContextContent context) throws ContentEvaluateException {
		StringBuffer buffer = new StringBuffer();
		for (Content content : contents) {
			String text = content.evaluate(context);
			buffer.append(text);
		}
		return buffer.toString();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Content content : contents) {
			buffer.append(content.toString());
		}
		return buffer.toString();
	}
}
