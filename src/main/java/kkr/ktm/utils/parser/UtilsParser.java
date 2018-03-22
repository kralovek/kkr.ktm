package kkr.ktm.utils.parser;

public class UtilsParser {
	public static int countSpace(char[] chars, int iPos) {
		int count = 0;
		for (; iPos < chars.length && isSpace(chars[iPos]); iPos++, count++) {
		}
		return count;
	}

	public static int countName(char[] chars, int iPos) {
		int count = 0;
		for (; iPos < chars.length && isName(chars[iPos]); iPos++, count++) {
		}
		return count;
	}

	public static boolean isSpace(char c) {
		return Character.isWhitespace(c);
	}

	public static boolean isNameStart(char c) {
		return false //
				|| 'a' <= c && c <= 'z' //
				|| 'A' <= c && c <= 'Z' //
				|| c == '_' //
		;
	}

	public static boolean isName(char c) {
		return isNameStart(c) || '0' <= c && c <= '9';
	}

}
