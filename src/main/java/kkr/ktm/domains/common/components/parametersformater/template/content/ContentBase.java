package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.Map;
import java.util.regex.Pattern;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueDecimal;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueInteger;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueText;
import kkr.ktm.domains.common.components.parametersformater.template.Position;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;

public abstract class ContentBase {
	private static final Pattern PATTERN_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_.]*$");

	protected Position position;

	public ContentBase(Position position) {
		super();
		this.position = position;
	}

	protected void checkUnknownAttributes(TagType tag, Map<String, String> attributes) throws ContentParseException {
		if (!attributes.isEmpty()) {
			throw new ContentParseException(position, "Unknown attributes "
					+ UtilsString.listToString(attributes.keySet(), null, null, ",") + " in the tag " + tag);
		}
	}

	public Position getPosition() {
		return position;
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

	protected Integer toInteger(Value value) {
		if (value instanceof ValueText) {
			try {
				return Integer.parseInt(((ValueText) value).getValue());
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		if (value instanceof ValueInteger) {
			return ((ValueInteger) value).getValue().intValue();
		}
		if (value instanceof ValueDecimal) {
			double valueDecimal = ((ValueDecimal) value).getValue();
			int valueInt = (int) valueDecimal;
			if (valueDecimal == (double) valueInt) {
				return valueInt;
			}
		}
		return null;
	}

	protected String toStringAttribute(String name, String value) {
		return name + "=\"" + value + "\"";
	}
}
