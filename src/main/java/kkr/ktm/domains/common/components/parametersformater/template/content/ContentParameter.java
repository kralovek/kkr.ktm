package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.context.content.ContextContent;
import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;
import kkr.ktm.domains.common.components.formatter.bytype.FormatterFactoryByType;
import kkr.ktm.domains.common.components.parametersformater.template.Position;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;

public class ContentParameter extends ContentTagBase implements Content {
	private static final Logger LOG = Logger.getLogger(ContentParameter.class);
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_NAME = "NAME";

	public static final String ATTR_FORMAT_DATE = "FORMAT-DATE";
	public static final String ATTR_FORMAT_DECIMAL = "FORMAT-DECIMAL";
	public static final String ATTR_FORMAT_INTEGER = "FORMAT-INTEGER";
	public static final String ATTR_FORMAT_STRING = "FORMAT-STRING";
	public static final String ATTR_FORMAT_BOOLEAN = "FORMAT-BOOLEAN";

	protected String name;
	protected Formatter formatter;
	private String[] indexes;

	private String formatString;
	private String formatBoolean;
	private String formatInteger;
	private String formatDecimal;
	private String formatDate;

	public ContentParameter(Position position, Map<String, String> attributes, FormatterFactoryByType formatterFactory)
			throws ContentParseException, BaseException {
		super(position, TagType.PARAMETER);
		LOG.trace("BEGIN");
		try {

			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// NAME
			//
			name = attributeName(true, ATTR_NAME, attributesLocal.remove(ATTR_NAME));

			//
			// INDEXES
			//
			indexes = attributeNames(false, ATTR_INDEXES, attributesLocal.remove(ATTR_INDEXES));

			//
			// FORMATTER
			//
			formatter = attributeFormat( //
					(formatString = attributesLocal.remove(ATTR_FORMAT_STRING)), //
					(formatBoolean = attributesLocal.remove(ATTR_FORMAT_BOOLEAN)), //
					(formatInteger = attributesLocal.remove(ATTR_FORMAT_INTEGER)), //
					(formatDecimal = attributesLocal.remove(ATTR_FORMAT_DECIMAL)), //
					(formatDate = attributesLocal.remove(ATTR_FORMAT_DATE)), //
					formatterFactory);

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void validate(Context context) throws ContentParseException {
		validateIndexes(context, ATTR_INDEXES, indexes);
	}

	public String evaluate(Context context) throws ContentEvaluateException {
		ContextContent contextContent = (ContextContent) context;
		Integer[] indexValues = contextContent.getContextIndex().evaluateIndexes(indexes);
		Object value = contextContent.getParameter(name, indexValues);
		try {
			String valueFormated = formatter.format(value);
			return valueFormated;
		} catch (FormatterException ex) {
			throw new ContentEvaluateException(position,
					"[" + TAG + "]: Cannot format the value " + value + " Problem: " + ex.getMessage(), ex);
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[').append(TAG);
		buffer.append(" ").append(toStringAttribute(ATTR_NAME, name));
		if (indexes != null && indexes.length != 0) {
			buffer.append(" ")
					.append(toStringAttribute(ATTR_INDEXES, UtilsString.arrayToString(indexes, null, null, ",")));
		}

		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_STRING, formatString));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_BOOLEAN, formatBoolean));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_INTEGER, formatInteger));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_DECIMAL, formatDecimal));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_DATE, formatDate));
		buffer.append(']');
		return buffer.toString();
	}
}
