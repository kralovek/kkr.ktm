package kkr.ktm.domains.tests.components.valueparser.generic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.common.utils.UtilsNumber;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.tests.components.valueparser.DataType;
import kkr.ktm.domains.tests.components.valueparser.Flag;
import kkr.ktm.domains.tests.components.valueparser.ValueParseException;
import kkr.ktm.domains.tests.components.valueparser.ValueParser;
import kkr.ktm.utils.parser.Position;
import kkr.ktm.utils.parser.UtilsParser;

public class ValueParserGeneric extends ValueComparator implements ValueParser {
	private static final Logger LOG = Logger.getLogger(ValueParserGeneric.class);

	private static final char CHAR_PARAMETER_OPEN = '<';
	private static final char CHAR_PARAMETER_CLOSE = '>';

	private static final char CHAR_PATTERN_QUOT = '#';
	private static final char CHAR_TEXT_QUOT = '"';

	private static final char CHAR_ARRAY_OPEN = '[';
	private static final char CHAR_ARRAY_CLOSE = ']';
	private static final char CHAR_ARRAY_SEPARATOR = '|';

	public Number parseValueNumber(String value) throws IllegalArgumentException {
		try {
			double d = Double.parseDouble(value);
			return UtilsNumber.reduceNumber(d);
		} catch (NumberFormatException ex) {
			// Nothing to do
		}
		throw new IllegalArgumentException("Argument's value cannot be converted to a number: " + value);
	}

	public Boolean parseValueBoolean(String value) throws IllegalArgumentException {
		if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
			return Boolean.TRUE;
		} else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
			return Boolean.FALSE;
		}
		throw new IllegalArgumentException("Argument's value cannot be converted to a boolean: " + value);
	}

	public Date parseValueDate(String value) throws IllegalArgumentException {
		for (DateFormat dateFormat : dateFormats) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException ex) {
				// Nothing to do
			}
		}
		throw new IllegalArgumentException("Argument's value cannot be converted to a date: " + value);
	}

	public Object parseValue(String content) throws ValueParseException {
		Object retval = parseValueFlag(content, null, false, false);
		return retval;
	}

	public Object parseValueFlag(String content, Collection<Flag> outputParameters) throws ValueParseException {
		Object retval = parseValueFlag(content, null, true, true);
		return retval;
	}

	private Object parseValueFlag(String content, Collection<Flag> outputParameters, boolean allowFlags,
			boolean allowPattern) throws ValueParseException {
		Position position = new Position(content);

		char[] chars = content.toCharArray();

		int iPos = UtilsParser.countSpace(chars, 0);

		Map<Flag, String> parameters = new LinkedHashMap<Flag, String>();

		iPos += parseParameters(position.movePosition(iPos), content, parameters);

		iPos += UtilsParser.countSpace(chars, iPos);

		String value = content.substring(iPos).trim();
		Object object = parseValue(position.movePosition(iPos), value);

		if (!parameters.isEmpty()) {
			return object;
		}

		if (parameters.containsKey(Flag.DT)) {
			String datatype = parameters.get(Flag.DT);
			try {
				DataType dataType = DataType.valueOf(datatype);
				object = convertValue(position, dataType, parameters.containsKey(Flag.NOREDUCE), allowPattern, object);
			} catch (Exception ex) {
				throw new ValueParseException(position, "Unknown Data Type: " + datatype);
			}
			parameters.remove(Flag.DT);
		}

		if (!parameters.isEmpty()) {
			if (!allowFlags) {
				throw new ValueParseException(position,
						"Flags are not allowed: " + UtilsString.toStringCollection(parameters.keySet(), "<", ">", ","));
			}
			outputParameters.addAll(parameters.keySet());
		}

		return object;
	}

	private Object convertValue(Position position, DataType dataType, boolean reduce, boolean allowPattern,
			Object object) throws ValueParseException {
		if (object == null) {
			return null;
		}

		if (object.getClass().isArray()) {
			return convertArray(position, dataType, reduce, allowPattern, (Object[]) object);
		}

		if (object instanceof Pattern) {
			if (!allowPattern) {
				throw new ValueParseException(position, "Pattern is prohibited in this value: " + object.toString());
			}

			if (dataType == DataType.STRING) {
				return object;
			} else {
				throw new ValueParseException(position,
						"Value converted to other data type than String may not contain Pattern: " + object.toString());
			}
		}

		switch (dataType) {
		case STRING:
			return toString(position, (String) object);
		case BOOLEAN:
			return toBoolean(position, (String) object);
		case INTEGER:
			return toInteger(position, (String) object);
		case DECIMAL:
			return toDecimal(position, (String) object);
		case DATE:
			return toDate(position, (String) object);
		default:
			throw new ValueParseException(position, "Unsupported datatype: " + dataType);
		}
	}

	private Object convertArray(Position position, DataType dataType, boolean reduce, boolean allowPattern,
			Object[] array) throws ValueParseException {
		if (array.length == 0) {
			return null;
		}
		if (reduce && array.length == 1) {
			return convertValue(position, dataType, reduce, allowPattern, array[0]);
		}
		Object[] retval = new Object[array.length];
		for (int i = 0; i < array.length; i++) {
			retval[i] = convertValue(position, dataType, reduce, allowPattern, array[0]);
		}
		return retval;
	}

	private Object parseValue(Position position, String content) throws ValueParseException {
		if (!content.isEmpty()) {
			if (content.charAt(0) == CHAR_PATTERN_QUOT) {
				return parseValuePattern(position, content);
			}
			if (content.charAt(0) == CHAR_ARRAY_OPEN) {
				return parseValueArray(position, content);
			}
			if (content.charAt(0) == CHAR_TEXT_QUOT) {
				return parseValueText(position, content);
			}
		}
		return parseValueOther(position, content);
	}

	private Object[] parseValueArray(Position position, String content) throws ValueParseException {
		if (content.charAt(0) != CHAR_ARRAY_OPEN || content.charAt(content.length() - 1) != CHAR_ARRAY_CLOSE) {
			throw new ValueParseException(position, "Pattern must be closed in {}");
		}

		Collection<Object> parts = new ArrayList<Object>();

		char[] chars = content.toCharArray();

		int iPos = 1;
		int iPosBegin = iPos;
		int open = 0;
		Character quotation = null;
		boolean escaped = false;
		for (; iPos < chars.length - 1; iPos++) {
			if (escaped) {
				escaped = false;
				continue;
			}
			if (chars[iPos] == '\\') {
				escaped = true;
				continue;
			}

			if (quotation != null) {
				if (chars[iPos] == quotation) {
					quotation = null;
					continue;
				}
				continue;
			}

			if (chars[iPos] == CHAR_PATTERN_QUOT || chars[iPos] == CHAR_TEXT_QUOT) {
				quotation = chars[iPos];
				continue;
			}

			if (chars[iPos] == CHAR_ARRAY_OPEN) {
				open++;
				continue;
			}
			if (chars[iPos] == CHAR_ARRAY_CLOSE) {
				open--;
				continue;
			}

			if (open == 0 && chars[iPos] == CHAR_ARRAY_SEPARATOR) {
				String item = content.substring(iPosBegin, iPos);
				Object object = parseValue(position.movePosition(iPosBegin), item);
				parts.add(object);
				iPosBegin = iPos + 1;
			}
		}

		if (escaped) {
			throw new ValueParseException(position.movePosition(iPos),
					"Value is terminated by escape character: '" + content + "'");
		}

		if (open > 0) {
			throw new ValueParseException(position.movePosition(iPos), "Missing closing ']': '" + content + "'");
		}

		if (open < 0) {
			throw new ValueParseException(position.movePosition(iPos), "Missing opening ']': '" + content + "'");
		}

		if (quotation != null) {
			throw new ValueParseException(position.movePosition(iPos), "Missing closing quotation '" + quotation + "'");
		}

		String item = content.substring(iPosBegin, iPos);
		Object object = parseValue(position.movePosition(iPosBegin), item);
		parts.add(object);

		Object[] retval = parts.toArray();

		return retval;
	}

	private Pattern parseValuePattern(Position position, String content) throws ValueParseException {
		if (content.charAt(0) != CHAR_PATTERN_QUOT || content.charAt(content.length() - 1) != CHAR_PATTERN_QUOT) {
			throw new ValueParseException(position,
					"Pattern must be closed in " + CHAR_PATTERN_QUOT + CHAR_PATTERN_QUOT);
		}
		String mask = content.substring(1, content.length() - 1);
		try {
			Pattern pattern = Pattern.compile(mask);
			return pattern;
		} catch (Exception ex) {
			throw new ValueParseException(position, "Bad format of the pattern: '" + mask + "'");
		}
	}

	private String parseValueText(Position position, String content) throws ValueParseException {
		if (content.charAt(0) != '"' || content.charAt(content.length() - 1) != '"') {
			throw new ValueParseException(position, "Text is not closed in quotations \"\"");
		}
		StringBuffer buffer = new StringBuffer(content.substring(1, content.length() - 1));
		boolean escaped = false;
		for (int i = 0; i < buffer.length(); i++) {
			if (escaped) {
				escaped = false;
				continue;
			}
			if (buffer.charAt(i) == '\\') {
				escaped = true;
				buffer.deleteCharAt(i);
				i--;
				continue;
			}
		}

		if (escaped) {
			throw new ValueParseException(position, "Text value is terminated by escape symbol '\\'");
		}

		if (buffer.length() != 0) {
			return buffer.toString();
		} else {
			return null;
		}
	}

	private Object parseValueOther(Position position, String content) {
		if (!content.isEmpty()) {
			return content;
		} else {
			return null;
		}
	}

	private int parseParameters(Position position, String content, Map<Flag, String> flags) throws ValueParseException {

		int iPos = 0;

		char[] chars = content.toCharArray();

		while (iPos < chars.length && chars[iPos] == CHAR_PARAMETER_OPEN) {

			iPos++;
			iPos += UtilsParser.countSpace(chars, iPos);

			int iPosBegin = iPos;
			if (!UtilsParser.isNameStart(chars[iPos])) {
				throw new ValueParseException(position.movePosition(iPos), "Bad name start character: " + chars[iPos]);
			}

			iPos++;
			iPos += UtilsParser.countName(chars, iPos);

			String name = content.substring(iPosBegin, iPos);
			Flag flag;
			try {
				flag = Flag.valueOf(name);
			} catch (Exception ex) {
				throw new ValueParseException(position.movePosition(iPos), "Unsupporeted flag: " + name);
			}

			if (flags.containsKey(flag)) {
				throw new ValueParseException(position.movePosition(iPos), "Dupicated flag: " + flag);
			}

			iPos += UtilsParser.countSpace(chars, iPos);

			if (chars[iPos] == CHAR_PARAMETER_CLOSE) {
				flags.put(flag, null);
				iPos++;
				continue;
			}

			if (chars[iPos] != '=') {
				throw new ValueParseException(position.movePosition(iPos - position.getPosition()), "Missing '='");
			}
			iPos++;
			iPosBegin = iPos;

			iPos += UtilsParser.countSpace(chars, iPos);

			String value;
			if (chars[iPos] == '"') {
				iPos++;
				iPosBegin = iPos;
				boolean escaped = false;
				for (; iPos < chars.length && chars[iPos] != '"'; iPos++) {
					if (escaped) {
						escaped = false;
						continue;
					}
					if (chars[iPos] == '\\') {
						escaped = true;
						continue;
					}
				}

				if (escaped) {
					throw new ValueParseException(position.movePosition(iPos - position.getPosition()),
							"Value is terminated by escape character '\\'");
				}

				if (chars[iPos] != '"') {
					throw new ValueParseException(position.movePosition(iPos - position.getPosition()),
							"Missing closing '\"'");
				}

				value = content.substring(iPosBegin, iPos);

				iPos++;
				iPos += UtilsParser.countSpace(chars, iPos);
			} else {
				iPosBegin = iPos;
				for (; iPos < chars.length && chars[iPos] != CHAR_PARAMETER_CLOSE; iPos++) {
					if (chars[iPos] == '"') {
						throw new ValueParseException(position.movePosition(iPos - position.getPosition()),
								"Parameter value may be without enclosing '\"', but in this case the '\"'may not be in the value");
					}
				}
				value = content.substring(iPosBegin, iPos).trim();
			}

			if (chars[iPos] == CHAR_PARAMETER_CLOSE) {
				flags.put(flag, value);
			} else {
				throw new ValueParseException(position.movePosition(iPos - position.getPosition()),
						"Missing closing '" + CHAR_PARAMETER_CLOSE + "'");
			}
			iPos++;
			iPos += UtilsParser.countSpace(chars, iPos);
		}
		return iPos - position.getPosition();
	}

	private Long toInteger(Position position, String value) throws ValueParseException {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			throw new ValueParseException(position, "Value is not an Integer: '" + value + "'");
		}
	}

	private Double toDecimal(Position position, String value) throws ValueParseException {
		try {
			value.replace(',', '.');
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			throw new ValueParseException(position, "Value is not a Decimal: '" + value + "'");
		}
	}

	private Boolean toBoolean(Position position, String value) throws ValueParseException {
		try {
			return Boolean.parseBoolean(value);
		} catch (NumberFormatException ex) {
			throw new ValueParseException(position, "Value is not an Integer: '" + value + "'");
		}
	}

	private Date toDate(Position position, String value) throws ValueParseException {
		for (DateFormat dateFormat : dateFormats) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException ex) {
				continue;
			}
		}
		throw new ValueParseException(position, "Value is not a Date: '" + value + "'");
	}

	private String toString(Position position, String value) throws ValueParseException {
		return value;
	}

	public static final void main(String[] argv) throws Exception {
		ValueParserGeneric valueParserGeneric = new ValueParserGeneric();
		valueParserGeneric.config();

		String text = "<NOCASE><DT= INT >[123|[456|789]|#[a-zA-Z_]{3}([01]|56)#|END]";

		Collection<Flag> parameters = new ArrayList<Flag>();

		Object object = valueParserGeneric.parseValueFlag(text, parameters);

		System.out.println(object);
	}
}
