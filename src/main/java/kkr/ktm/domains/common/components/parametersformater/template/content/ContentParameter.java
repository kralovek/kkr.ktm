package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.parametersformater.template.Position;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.format.Format;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatBase;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatType;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;

public class ContentParameter extends ContentBase implements Content {
	private static final Logger LOG = Logger.getLogger(ContentParameter.class);
	public static final TagType TAG = TagType.PARAMETER;
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_FORMAT = "FORMAT";
	public static final String ATTR_FORMAT_TYPE = "FORMAT-TYPE";

	protected String name;
	protected Format format;
	private String[] indexes;

	public ContentParameter(Position position, Map<String, String> attributes) throws ContentParseException {
		super(position);
		LOG.trace("BEGIN");
		try {

			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// NAME
			//
			if (attributesLocal.containsKey(ATTR_NAME)) {
				String attributeValue = attributesLocal.remove(ATTR_NAME);
				if (!isName(attributeValue)) {
					throw new ContentParseException(position, "Cannot evaluate the value of the attribute [" + TAG + " "
							+ ATTR_NAME + "] " + " as a parameter name: " + attributeValue);
				}
				name = attributeValue;
			} else {
				throw new ContentParseException(position, "Missing attribute [" + TAG + " " + ATTR_NAME + "]");
			}

			//
			// INDEXES
			//
			if (attributesLocal.containsKey(ATTR_INDEXES)) {
				String attributeValue = attributesLocal.remove(ATTR_INDEXES);

				try {
					indexes = toArrayValue(attributeValue);
				} catch (IllegalArgumentException ex) {
					throw new ContentParseException(position, "Cannot evaluate the value of the attribute '"
							+ ATTR_INDEXES + "' as a comma separated list of index names: " + ex.getMessage());
				}
			}

			//
			// FORMAT TYPE
			//
			FormatType formatType = null;
			if (attributesLocal.containsKey(ATTR_FORMAT_TYPE)) {
				String attributeValue = attributesLocal.remove(ATTR_FORMAT_TYPE);
				try {
					formatType = FormatType.valueOf(attributeValue);
				} catch (Exception ex) {
					throw new ContentParseException(position, "Bad format-type of the attribute [" + TAG + " "
							+ ATTR_FORMAT_TYPE + "]" + " value: " + attributeValue + " problem: " + ex.getMessage());
				}
			}

			//
			// FORMAT
			//
			{
				String attributeValue = attributesLocal.remove(ATTR_FORMAT);
				if (formatType == null && !UtilsString.isEmpty(attributeValue)) {
					formatType = FormatType.VALUE;
				} else {
					formatType = FormatType.AUTO;
				}
				try {
					format = FormatBase.newFormat(formatType, attributeValue);
				} catch (Exception ex) {
					throw new ContentParseException(position,
							"Bad format of the attribute [" + TAG + " " + ATTR_FORMAT + "=\"" + attributeValue + "\"]"
									+ " value: " + attributeValue + " problem: " + ex.getMessage());
				}
			}

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public String evaluate(ContextContent context) throws ContentEvaluateException {
		Value value = context.getParameter(name, indexes);
		try {
			String valueFormated = format.format(value);
			return valueFormated;
		} catch (Exception ex) {
			throw new ContentEvaluateException(position, "Bad format string for a " + value.getType() + " value [" + TAG
					+ " " + ATTR_FORMAT + "]: " + format.toString(), ex);
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

		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_TYPE, format.getType().toString()));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT, format.toString()));
		buffer.append(']');
		return buffer.toString();
	}
}
