package kkr.ktm.domains.common.components.parametersformater.template2.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template2.Position;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template2.part.TagType;

public class ContentLoop extends ContentBase implements Content, Open {
	private static final Logger LOG = Logger.getLogger(ContentLoop.class);
	public static final TagType TAG = TagType.LOOP;
	public static final String ATTR_INDEX = "INDEX";
	public static final String ATTR_INDEXES = "INDEXES";
	public static final String ATTR_NAME = "NAME";
	public static final String ATTR_TYPE = "TYPE";

	public static enum Type {
		COUNT, LENGTH
	};

	private String index;
	private String[] indexes;
	private String name;
	private Type type;

	private Content content;

	public ContentLoop(Position position, Map<String, String> attributes) throws ContentParseException {
		super(position);
		LOG.trace("BEGIN");
		try {
			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// INDEX
			//
			if (attributesLocal.containsKey(ATTR_INDEX)) {
				String attributeValue = attributesLocal.remove(ATTR_INDEX);
				if (!isName(attributeValue)) {
					throw new ContentParseException(position, "Cannot evaluate the value of the attribute '"
							+ ATTR_INDEX + "' as a parameter name: " + name);
				}
				index = attributeValue;
			} else {
				throw new ContentParseException(null, "Missing attribute [" + TAG + " " + ATTR_INDEX + "]");
			}
			//
			// NAME
			//
			if (attributes.containsKey(ATTR_NAME)) {
				String attributeValue = attributesLocal.remove(ATTR_NAME);
				if (!isName(attributeValue)) {
					throw new ContentParseException(position,
							"Cannot evaluate the attribute [" + TAG + " " + ATTR_NAME + "] as a parameter name");
				}
				name = attributeValue;
			} else {
				throw new ContentParseException(null, "Missing attribute [" + TAG + " " + ATTR_NAME + "]");
			}

			//
			// INDEXES
			//
			if (attributes.containsKey(ATTR_INDEXES)) {
				String attributeValue = attributesLocal.remove(ATTR_INDEXES);
				try {
					indexes = toArrayValue(attributeValue);
				} catch (IllegalArgumentException ex) {
					throw new ContentParseException(position, "Cannot evaluate the value of the attribute '"
							+ ATTR_INDEXES + "' as a comma separated list of index names: " + ex.getMessage());
				}
			}
			//
			// TYPE
			//
			if (attributes.containsKey(ATTR_TYPE)) {
				String attributeValue = attributesLocal.remove(ATTR_TYPE);
				try {
					type = Type.valueOf(attributeValue);
				} catch (Exception ex) {
					throw new ContentParseException(position, "Bad value of the attribute [" + TAG + " " + ATTR_TYPE
							+ "]" + " value: " + attributeValue + " problem: " + ex.getMessage());
				}
			} else {
				throw new ContentParseException(position, "Missing attribute [" + TAG + " " + ATTR_TYPE + "]");
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

	public String getIndex() {
		return index;
	}

	public String evaluate(ContextContent context) throws ContentEvaluateException {
		Integer count;
		switch (type) {
		case COUNT:
			Value valueParameter = context.getParameter(name, indexes);
			count = toInteger(valueParameter);
			if (count == null || count < 0) {
				throw new ContentEvaluateException(position, "The value of the parameter " + name
						+ " must be a non negativ integer: " + valueParameter.toString());
			}
			break;
		case LENGTH:
			count = context.getParameterSize(name, indexes);
			break;
		default:
			throw new ContentEvaluateException(position, "Unsupported LOOP type: " + type);
		}

		StringBuffer buffer = new StringBuffer();

		context.addIndex(index, 0);

		if (content != null) {
			for (int indexValue = 0; indexValue < count; indexValue++) {
				context.updateIndex(index, indexValue);
				String text = content.evaluate(context);
				buffer.append(text);
			}
		}

		context.removeIndex(index);

		return buffer.toString();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[').append(TAG);
		buffer.append(" ").append(toStringAttribute(ATTR_NAME, name));
		if (indexes != null && indexes.length != 0) {
			buffer.append(" ")
					.append(toStringAttribute(ATTR_INDEXES, UtilsString.arrayToString(indexes, null, null, ",")));
		}

		buffer.append(" ").append(toStringAttribute(ATTR_INDEX, index.toString()));

		buffer.append(" ").append(toStringAttribute(ATTR_TYPE, type.toString()));
		buffer.append(']');
		if (content != null) {
			buffer.append(content.toString());
		}
		buffer.append("[END]");
		return buffer.toString();
	}
}
