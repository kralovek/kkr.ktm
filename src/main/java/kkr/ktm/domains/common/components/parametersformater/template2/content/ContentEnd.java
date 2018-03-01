package kkr.ktm.domains.common.components.parametersformater.template2.content;

import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.parametersformater.template2.Position;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template2.part.TagType;

public class ContentEnd extends ContentBase implements Content, Close {
	private static final Logger LOG = Logger.getLogger(ContentEnd.class);
	public static final TagType TAG = TagType.END;

	public ContentEnd(Position position, Map<String, String> attributes) throws ContentParseException {
		super(position);
		LOG.trace("BEGIN");
		try {
			checkUnknownAttributes(TAG, attributes);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public String evaluate(ContextContent context) throws ContentEvaluateException {
		return "[" + TAG + "]";
	}
}
