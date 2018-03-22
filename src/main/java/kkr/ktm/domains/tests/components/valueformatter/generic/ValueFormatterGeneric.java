package kkr.ktm.domains.tests.components.valueformatter.generic;

import java.util.Date;

import kkr.ktm.domains.tests.components.valueformatter.ValueFormatException;
import kkr.ktm.domains.tests.components.valueformatter.ValueFormatter;

public class ValueFormatterGeneric extends ValueFormatterGenericFwk implements ValueFormatter {
	private static final char CHAR_ESCAPE = '\\';
	private static final String CHAR_ARRAY_OPEN = "[";
	private static final String CHAR_ARRAY_CLOSE = "]";
	private static final String CHAR_ARRAY_SEPARATOR = "|";
	private static final String TAB = "    ";

	public String formatValue(Object object) throws ValueFormatException {
		return formatValue(object, 0);
	}

	private String formatValue(Object object, int level) throws ValueFormatException {
		if (object == null) {
			return "";
		}

		String tab = syntaxFormatting ? toStringLevel(level) : "";

		if (object.getClass().isArray()) {
			return formatArray((Object[]) object, level);
		}

		if (object instanceof Date) {
			return tab + dateFormat.format(object);
		} else if (object instanceof Number) {
			return tab + numberFormat.format(object);
		} else if (object instanceof Boolean) {
			return object.toString();
		} else if (object instanceof String) {
			String string = (String) object;
			return tab + string //
					.replace(CHAR_ARRAY_SEPARATOR, CHAR_ESCAPE + CHAR_ARRAY_SEPARATOR) //
					.replace(CHAR_ARRAY_OPEN, CHAR_ESCAPE + CHAR_ARRAY_OPEN) //
					.replace(CHAR_ARRAY_CLOSE, CHAR_ESCAPE + CHAR_ARRAY_CLOSE) //
			;
		} else {
			throw new ValueFormatException("Cannot format object " + object.getClass().getSimpleName());
		}
	}

	private String formatArray(Object[] array, int level) throws ValueFormatException {
		boolean containArray = false;
		String tab = "";
		if (syntaxFormatting) {
			tab = toStringLevel(level);
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null && array[i].getClass().isArray()) {
					containArray = true;
					break;
				}
			}
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append(tab).append(CHAR_ARRAY_OPEN);

		for (int i = 0; i < array.length; i++) {
			String value = formatValue(array[i], containArray ? level + 1 : 0);
			if (i != 0) {
				buffer.append(CHAR_ARRAY_SEPARATOR);
			}
			if (syntaxFormatting && containArray) {
				buffer.append("\n");
			}
			buffer.append(value);
		}

		if (syntaxFormatting && containArray) {
			buffer.append("\n").append(tab);
		}
		buffer.append(CHAR_ARRAY_CLOSE);
		return buffer.toString();
	}

	private String toStringLevel(int level) {
		if (level == 0) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < level; i++) {
			buffer.append(TAB);
		}
		return buffer.toString();
	}

	public static final void main(String[] argv) throws Exception {
		ValueFormatterGeneric valueFormatterGeneric = new ValueFormatterGeneric();
		valueFormatterGeneric.setSyntaxFormatting(true);
		valueFormatterGeneric.config();

		Object object = new Object[] { //
				12.67, //
				"abc", //
				new Object[] { //
						"zz", //
						new Object[] { //
								new Date(), //
								4567.29 //
						}, //
						876, //
						new Object[] { //
								"ah|oj", //
								"nazdar", //
								new Object[] { //
										"hovno", //
										6 //
								} //
						}, //
				}, //
				new Date() //
		};

		String text = valueFormatterGeneric.formatValue(object);

		System.out.println(text);
	}
}
