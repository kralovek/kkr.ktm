package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.Map;
import java.util.regex.Pattern;

import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.context.name.ContextName;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;
import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.bytype.FormatterFactoryByType;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;
import kkr.ktm.utils.parser.Position;

public abstract class ContentTagBase extends ContentBase {
	private static final Pattern PATTERN_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_.]*$");

	protected TagType TAG;

	public ContentTagBase(Position position, TagType tag) {
		super(position);
		this.TAG = tag;
	}

	private String checkAttributeMandatory(boolean mandatory, String name, String value) throws ContentParseException {
		if (name == null) {
			if (mandatory) {
				throw new ContentParseException(position, "Attribute [" + TAG + " " + name + "] is mandatory");
			}
			return null;
		}
		if (value == null || value.isEmpty()) {
			if (mandatory) {
				throw new ContentParseException(position, "Attribute [" + TAG + " " + name + "] may not be empty");
			}
			return null;
		}
		return value;
	}

	public void validateIndexes(Context context, String attribute, String[] indexes) throws ContentParseException {
		ContextName contextName = (ContextName) context;
		if (indexes != null && indexes.length != 0) {
			for (String index : indexes) {
				if (!contextName.isName(index)) {
					throw new ContentParseException(position, "Bad value of attribute [" + TAG + " " + attribute
							+ "]: Name of index is not defined by the parent: " + index);
				}
			}
		}
	}

	protected <T> T attributeEnum(boolean mandatory, String name, String value, T[] enm) throws ContentParseException {
		value = checkAttributeMandatory(mandatory, name, value);
		if (value == null) {
			return null;
		}
		for (T t : enm) {
			if (value.equals(t.toString())) {
				return t;
			}
		}
		throw new ContentParseException(position, "Attribute [" + TAG + " " + name + "] has bad value: '" + value
				+ "' Allowed values: " + UtilsString.arrayToString(enm, null, null, ","));
	}

	protected String attributeName(boolean mandatory, String name, String value) throws ContentParseException {
		value = checkAttributeMandatory(mandatory, name, value);
		if (value == null) {
			return null;
		}
		if (!PATTERN_NAME.matcher(value).matches()) {
			throw new ContentParseException(position,
					"Attribute [" + TAG + " " + name + "] must be a name: '" + value + "'");
		}
		return value;
	}

	protected Formatter attributeFormat(String valueString, String valueBoolean, String valueInteger,
			String valueDecimal, String valueDate, FormatterFactoryByType formatterFactory)
			throws ContentParseException, BaseException {
		try {
			return formatterFactory.createFormatter(valueString, valueBoolean, valueInteger, valueDecimal, valueDate);
		} catch (ConfigurationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ContentParseException(position, "Attribute [" + TAG + "] bad value in some format string", ex);
		}
	}

	protected String[] attributeNames(boolean mandatory, String name, String value) throws ContentParseException {
		value = checkAttributeMandatory(mandatory, name, value);
		if (value == null) {
			return null;
		}
		String[] array = value.split(",");
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i].isEmpty()) {
				if (array.length == 1) {
					return null;
				}
				throw new ContentParseException(position,
						"Attribute [" + TAG + " " + name + "] must be composed with not empty names: '" + value + "'");
			}
			if (!isName(array[i])) {
				throw new ContentParseException(position,
						"Attribute [" + TAG + " " + name + "] must be composed with not empty names: '" + value + "'");
			}
		}
		return array;

	}

	protected Expression attributeExpression(boolean mandatory, String name, String value,
			ExpressionParser expressionParser) throws ContentParseException {
		value = checkAttributeMandatory(mandatory, name, value);
		if (value == null) {
			return null;
		}
		try {
			Expression expression = expressionParser.parseExpression(value);
			return expression;
		} catch (BaseException ex) {
			throw new ContentParseException(position, "Attribute [" + TAG + " " + name
					+ "] does not contain a valid expression: '" + value + "' Problem: " + ex.getMessage(), ex);
		}
	}

	protected void checkUnknownAttributes(TagType tag, Map<String, String> attributes) throws ContentParseException {
		if (!attributes.isEmpty()) {
			throw new ContentParseException(position, "Unknown attributes "
					+ UtilsString.listToString(attributes.keySet(), null, null, ",") + " in the tag " + tag);
		}
	}

	protected boolean isName(String text) {
		return PATTERN_NAME.matcher(text).matches();
	}

	protected String[] toArrayValue(String value) throws IllegalArgumentException {
		if (value == null) {
			return null;
		}
		String[] array = value.split(",");
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i].isEmpty()) {
				if (array.length == 1) {
					return null;
				}
				throw new IllegalArgumentException("One name of names list is empty");
			}
			if (!isName(array[i])) {
				throw new IllegalArgumentException("One name is in bad format");
			}
		}
		return array;
	}

	protected String toStringAttribute(String name, String value) {
		return name + "=\"" + value + "\"";
	}

	public String toStringTag(Map<String, String> attributes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[').append(TAG);
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			buffer.append(" ").append(toStringAttribute(entry.getKey(), entry.getValue()));
		}
		buffer.append("]");
		return buffer.toString();
	}
}
