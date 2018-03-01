package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.parametersformater.template.Position;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;

public class ContentIf extends ContentBase implements Content, Open {
	private static final Logger LOG = Logger.getLogger(ContentIf.class);
	public static final TagType TAG = TagType.IF;

	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_TYPE = "TYPE";
	public static final String ATTR_VALUE = "VALUE";

	public static enum Type {
		EQ, EMPTY, NE, NONEMPTY
	}

	private String name;
	private String[] indexes;
	private Type type;
	private String value;

	private Content content;

	public ContentIf(Position position, Map<String, String> attributes) throws ContentParseException {
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
					throw new ContentParseException(position,
							"Cannot evaluate the attribute [" + TAG + " " + ATTR_NAME + "] as a parameter name");
				}
				name = attributeValue;
			} else {
				throw new ContentParseException(position, "Missing attribute [" + TAG + " " + ATTR_NAME + "]");
			}

			//
			// VALUE
			//
			if (attributesLocal.containsKey(ATTR_VALUE)) {
				String attributeValue = attributesLocal.remove(ATTR_VALUE);
				value = attributeValue;
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
			} else {
				indexes = new String[0];
			}

			//
			// TYPE
			//
			if (attributesLocal.containsKey(ATTR_TYPE)) {
				String attributeValue = attributesLocal.remove(ATTR_TYPE);
				try {
					type = Type.valueOf(attributeValue);
				} catch (Exception ex) {
					throw new ContentParseException(position,
							"The value of the attribute [" + TAG + " " + ATTR_TYPE + "] must be one of: " //
									+ UtilsString.arrayToString(Type.values(), null, null, ","));
				}
			} else {
				throw new ContentParseException(position, "Attribute [" + TAG + " " + ATTR_NAME + "] must be defined.");
			}

			switch (type) {
			case EQ:
			case NE:
				if (value == null) {
					throw new ContentParseException(position,
							"The attribut " + ATTR_VALUE + " is expected when the value of the attribute [" + TAG + " "
									+ ATTR_TYPE + "=\"" + type + "\"]");
				}
				break;

			case EMPTY:
			case NONEMPTY:
				if (value != null) {
					throw new ContentParseException(position,
							"The attribut " + ATTR_VALUE + " may not be used when the value of the attribute [" + TAG
									+ " " + ATTR_TYPE + "=\"" + type + "\"]");
				}
				break;
			}

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String evaluate(ContextContent context) throws ContentEvaluateException {
		Value value = context.getParameter(name, indexes);

		boolean evaluate = true;

		switch (type) {
		case EMPTY:
			evaluate = value.isEmpty();
			break;
		case NONEMPTY:
			evaluate = !value.isEmpty();
			break;
		case EQ:
			evaluate = value.equals(value);
			break;
		case NE:
			evaluate = !value.equals(value);
			break;
		default:
			throw new IllegalStateException("Unsupported type of " + TAG + ": " + type);
		}

		if (context != null && evaluate) {
			return content.evaluate(context);
		} else {
			return "";
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

		buffer.append(" ").append(toStringAttribute(ATTR_TYPE, type.toString()));
		buffer.append(']');
		if (content != null) {
			buffer.append(content.toString());
		}
		buffer.append("[END]");
		return buffer.toString();
	}
}
