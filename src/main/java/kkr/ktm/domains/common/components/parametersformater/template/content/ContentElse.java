package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;
import kkr.ktm.utils.parser.Position;

public class ContentElse extends ContentTagBase implements Content, Middle {
	private static final Logger LOG = Logger.getLogger(ContentEnd.class);

	public ContentElse(Position position, Map<String, String> attributes) throws ContentParseException {
		super(position, TagType.ELSE);
		LOG.trace("BEGIN");
		try {
			checkUnknownAttributes(TAG, attributes);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void validate(Context context) throws ContentParseException {
	}

	public String evaluate(Context context) throws ContentEvaluateException {
		throw new IllegalStateException("Method 'evaluate' is not implemented");
	}

	public String toString() {
		return "[" + TAG + "]";
	}
}
