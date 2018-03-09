package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.context.content.ContextContent;
import kkr.ktm.domains.common.components.context.name.ContextName;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;
import kkr.ktm.domains.common.components.parametersformater.template.value.UtilsValue;
import kkr.ktm.utils.parser.Position;

public class ContentLoop extends ContentTagBase implements Content, Open {
	private static final Logger LOG = Logger.getLogger(ContentLoop.class);
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
		super(position, TagType.LOOP);
		LOG.trace("BEGIN");
		try {
			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// NAME
			//
			name = attributeName(true, ATTR_NAME, attributesLocal.remove(ATTR_NAME));

			//
			// INDEX
			//
			index = attributeName(true, ATTR_INDEX, attributesLocal.remove(ATTR_INDEX));

			//
			// INDEXES
			//
			indexes = attributeNames(false, ATTR_INDEXES, attributesLocal.remove(ATTR_INDEXES));

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

	public void addContent(Content content) throws ContentParseException {
		if (this.content != null) {
			throw new ContentParseException(position, "Content already added to [" + TAG + "]");
		}
		this.content = content;
	}

	public String getIndex() {
		return index;
	}

	public void validate(Context context) throws ContentParseException {
		validateIndexes(context, ATTR_INDEXES, indexes);

		ContextName contextName = (ContextName) context;
		if (content != null) {
			if (contextName.isName(index)) {
				throw new ContentParseException(position, "Bad value of attribute [" + TAG + " " + ATTR_INDEX
						+ "]: Name of index is already used be somme parent: " + index);
			}
			contextName.addName(index);
			content.validate(context);
			contextName.removeName(index);
		}
	}

	public String evaluate(Context context) throws ContentEvaluateException {
		ContextContent contextContent = (ContextContent) context;
		Integer count;
		Integer[] indexValues = contextContent.getContextIndex().evaluateIndexes(indexes);
		switch (type) {
		case COUNT:
			Object valueParameter = contextContent.getParameter(name, indexValues);
			count = UtilsValue.toInteger(valueParameter);
			if (count == null || count < 0) {
				throw new ContentEvaluateException(position,
						"The value of the parameter " + name + " must be a non negativ integer: " + valueParameter);
			}
			break;
		case LENGTH:
			count = contextContent.getParameterSize(name, indexValues);
			break;
		default:
			throw new ContentEvaluateException(position, "Unsupported LOOP type: " + type);
		}

		StringBuffer buffer = new StringBuffer();

		contextContent.getContextIndex().addIndex(index, 0);

		if (content != null) {
			for (int indexValue = 0; indexValue < count; indexValue++) {
				contextContent.getContextIndex().updateIndex(index, indexValue);
				String text = content.evaluate(context);
				buffer.append(text);
			}
		}

		contextContent.getContextIndex().removeIndex(index);

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
