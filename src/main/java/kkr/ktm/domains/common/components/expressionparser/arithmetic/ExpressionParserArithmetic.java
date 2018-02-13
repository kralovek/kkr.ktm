package kkr.ktm.domains.common.components.expressionparser.arithmetic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ParseExpressionException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.expression.ExpressionFunction;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.expression.ExpressionNumber;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.expression.ExpressionOperator;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.expression.ExpressionParameter;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.level.Level;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.level.LevelAdd;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.Operator;

// 1^2 + 2*{A} + 3*{B}/{C} + (4*{A} + 5*7*{D}/(6 + {C})) + SIN(8)

public class ExpressionParserArithmetic extends ExpressionParserArithmeticFwk implements ExpressionParser {
	private static final Logger LOG = Logger.getLogger(ExpressionParserArithmetic.class);

	private static final Pattern PATTERN_NUMBER_START = Pattern.compile("^[0-9\\.].*");
	private static final Pattern PATTERN_NUMBER = Pattern.compile("^(0?|[1-9][0-9]*)(\\.[0-9]*)?$");
	private static final Pattern PATTERN_PARAMETER_START = Pattern.compile("^\\{.*");
	private static final Pattern PATTERN_PARAMETER = Pattern.compile("^\\{[a-z_A-Z][a-z_A-Z0-9]*\\}$");
	private static final Pattern PATTERN_FUNCTION_START = Pattern.compile("^[a-z_A-Z].*");
	private static final Pattern PATTERN_FUNCTION = Pattern.compile("^[a-z_A-Z][a-z_A-Z0-9]* *\\(.*\\)$");
	private static final Pattern PATTERN_PARENTHESE_START = Pattern.compile("^\\(.*");
	private static final Pattern PATTERN_PARENTHESE = Pattern.compile("^\\(.*\\)$");

	private static LevelAdd LEVEL = new LevelAdd();

	private static class Addition {
		private Position position;
		private String text;
		private Operator operator;

		public Addition(Position position, Operator operator) {
			this.position = position;
			this.operator = operator;
		}

		public Position getPosition() {
			return position;
		}

		public Operator getOperator() {
			return operator;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public String toString() {
			return "[" + position.toString() + "] '" + text + "'";
		}
	}

	private boolean isSpace(char c) {
		return Character.isWhitespace(c);
	}

	private int countSpaces(char[] chars, int offset) {
		int n = 0;
		for (int i = offset; i < chars.length && isSpace(chars[i]); i++, n++) {
		}
		return n;
	}

	private int countEndSpaces(char[] chars, int offset) {
		int n = 0;
		for (int i = chars.length - 1; i >= offset && isSpace(chars[i]); i--, n++) {
		}
		return n;
	}

	private Collection<Addition> parseAdditions(Position position, String text, Operator operatorType)
			throws ParseExpressionException {
		LOG.trace("BEGIN: {" + operatorType.getClass().getSimpleName() + "} [" + position.getPosition() + "] '" + text
				+ "'");
		try {
			Collection<Addition> retval = new ArrayList<Addition>();
			char[] chars = text.toCharArray();

			int openLevel = 0;
			int iPosOpen = 0;
			Addition cutCurrent = null;

			int iPos = 0;
			for (; iPos < chars.length; iPos++) {
				char c = chars[iPos];
				if (c == '(') {
					if (openLevel == 0) {
						iPosOpen = iPos;
					}
					openLevel++;

					if (cutCurrent == null) {
						cutCurrent = new Addition(position.movePosition(iPos), null);
						retval.add(cutCurrent);
					}

					continue;
				}
				if (c == ')') {
					openLevel--;
					if (openLevel < 0) {
						throw new ParseExpressionException(position.movePosition(iPos), "Missing opening parenthese");
					}
					continue;
				}

				if (openLevel != 0) {
					continue;
				}

				if (isSpace(c)) {
					continue;
				}

				Operator operator = operatorType.valueOfSymbol(c);

				if (cutCurrent == null) {
					if (operator != null) {
						cutCurrent = new Addition(position.movePosition(iPos + 1), operator);
					} else {
						cutCurrent = new Addition(position.movePosition(iPos), null);
					}
					retval.add(cutCurrent);
					continue;
				}

				if (operator != null) {
					cutCurrent.setText(text.substring( //
							cutCurrent.getPosition().getPosition() - position.getPosition(), //
							iPos));
					cutCurrent = new Addition(position.movePosition(iPos + 1), operator);
					retval.add(cutCurrent);
					continue;
				}
			}

			if (openLevel != 0) {
				throw new ParseExpressionException(position.movePosition(iPosOpen), "Missing closing parenthese");
			}

			if (cutCurrent != null) {
				cutCurrent.setText(text.substring( //
						cutCurrent.getPosition().getPosition() - position.getPosition(), //
						iPos));
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Expression parseParentheses(Position position, String text) throws ParseExpressionException {
		LOG.trace("BEGIN: [" + position.getPosition() + "] '" + text + "'");
		try {
			if (!PATTERN_PARENTHESE_START.matcher(text).matches()) {
				LOG.trace("OK");
				return null;
			}
			if (!PATTERN_PARENTHESE.matcher(text).matches()) {
				throw new ParseExpressionException(position,
						"Literal is not a parenthese closed expression: '" + text + "'");
			}
			String subtext = text.substring(1, text.length() - 1);
			Expression retval = parseOperator(position.movePosition(1), subtext, LEVEL);
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Expression parseNumber(Position position, String text) throws ParseExpressionException {
		LOG.trace("BEGIN: [" + position.getPosition() + "] '" + text + "'");
		try {
			if (!PATTERN_NUMBER_START.matcher(text).matches()) {
				LOG.trace("OK");
				return null;
			}

			if (!PATTERN_NUMBER.matcher(text).matches()) {
				throw new ParseExpressionException(position, "Literal is not a number: '" + text + "'");
			}

			double value = Double.parseDouble(text);
			Expression expressionNumber = new ExpressionNumber(value);
			LOG.trace("OK");
			return expressionNumber;
		} finally {
			LOG.trace("END");
		}
	}

	private Expression parseFunction(Position position, String text) throws ParseExpressionException {
		LOG.trace("BEGIN: [" + position.getPosition() + "] '" + text + "'");
		try {
			if (!PATTERN_FUNCTION_START.matcher(text).matches()) {
				LOG.trace("OK");
				return null;
			}

			if (!PATTERN_FUNCTION.matcher(text).matches()) {
				throw new ParseExpressionException(position, "Literal is not a function: '" + text + "'");
			}

			int iParentBegin = text.indexOf('(');
			int iParentEnd = text.lastIndexOf(')');

			String name = text.substring(0, iParentBegin).trim();
			String argument = text.substring(iParentBegin, iParentEnd + 1);

			Expression expression = parseOperator(position.movePosition(iParentBegin + 1), argument, LEVEL);

			try {
				ExpressionFunction expressionFunction = new ExpressionFunction(name, expression);
				LOG.trace("OK");
				return expressionFunction;
			} catch (Exception ex) {
				throw new ParseExpressionException(position, "Cannot evalueate function: " + name, ex);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private Expression parseParameter(Position position, String text) throws ParseExpressionException {
		LOG.trace("BEGIN: [" + position.getPosition() + "] '" + text + "'");
		try {
			if (!PATTERN_PARAMETER_START.matcher(text).matches()) {
				LOG.trace("OK");
				return null;
			}

			if (!PATTERN_PARAMETER.matcher(text).matches()) {
				throw new ParseExpressionException(position, "Literal is not a parameter: '" + text + "'");
			}
			String name = text.substring(1, text.length() - 1).trim();
			ExpressionParameter expressionParameter = new ExpressionParameter(name);
			LOG.trace("OK");
			return expressionParameter;
		} finally {
			LOG.trace("END");
		}
	}

	private Expression parseLiteral(Position position, String text) throws ParseExpressionException {
		LOG.trace("BEGIN: [" + position.getPosition() + "] '" + text + "'");
		try {
			char[] chars = text.toCharArray();
			int iPos = countSpaces(chars, 0);
			int iPosEnd = text.length() - countEndSpaces(chars, iPos);

			if (iPos != 0 || iPosEnd != text.length()) {
				position = position.movePosition(iPos);
				text = text.substring(iPos, iPosEnd);
			}

			if (text.length() == 0) {
				throw new ParseExpressionException(position, "Missing a literal");
			}

			Expression expression = parseParentheses(position, text);
			if (expression != null) {
				LOG.trace("OK");
				return expression;
			}

			expression = parseNumber(position, text);
			if (expression != null) {
				LOG.trace("OK");
				return expression;
			}

			expression = parseParameter(position, text);
			if (expression != null) {
				LOG.trace("OK");
				return expression;
			}

			expression = parseFunction(position, text);
			if (expression != null) {
				LOG.trace("OK");
				return expression;
			}

			throw new ParseExpressionException(position, "Unknown literal: '" + text + "'");

		} finally {
			LOG.trace("END");
		}
	}

	private Expression parseOperator(Position position, String text, Level level) throws ParseExpressionException {
		LOG.trace("BEGIN: [" + position.getPosition() + "] '" + text + "'");
		try {
			Collection<Addition> additions = parseAdditions(position, text, level.getOperator());

			LOG.debug("ADDITIONS: ");
			for (Addition addition : additions) {
				LOG.debug(" - " + addition.toString());
			}

			Expression expressionFirst = null;
			ExpressionOperator expressionOperator = null;
			for (Addition addition : additions) {
				Expression expression = null;
				if (level.nextLevel() != null) {
					expression = parseOperator(addition.getPosition(), addition.getText(), level.nextLevel());
				} else {
					expression = parseLiteral(addition.getPosition(), addition.getText());
				}

				if (expressionFirst == null) {
					expressionFirst = level.first(addition.getPosition(), addition.getOperator(), expression);
					continue;
				}

				expressionOperator = new ExpressionOperator( //
						addition.getOperator(), //
						expressionOperator != null ? expressionOperator : expressionFirst, //
						expression);
			}

			if (expressionOperator == null) {
				if (expressionFirst == null) {
					throw new ParseExpressionException(position, "Missing expression");
				}
				LOG.trace("OK");
				return expressionFirst;
			}

			LOG.trace("OK");
			return expressionOperator;
		} finally {
			LOG.trace("END");
		}
	}

	public Expression parseExpression(String text) throws ParseExpressionException {
		Position position = new Position();
		Expression retval = parseOperator(position, text, LEVEL);
		return retval;
	}

	public static final void main(String[] argv) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String text = "2^({A} + 2) + 2.5*{B} + 3*{C}/{D} + (4*{E} + 5*7*{F}/(6 + {G})) + sin({H})";

			LOG.info("Text: " + text);

			ExpressionParserArithmetic parser = new ExpressionParserArithmetic();
			parser.config();
			Expression expression = parser.parseExpression(text);

			LOG.info("Expression: " + text);
			LOG.info("Formated:   " + expression.toString());

			Map<String, Double> parameters = new HashMap<String, Double>();

			parameters.put("A", 1.);
			parameters.put("B", 2.);
			parameters.put("C", 3.);
			parameters.put("D", 4.);
			parameters.put("E", 5.);
			parameters.put("F", 6.);
			parameters.put("G", 7.);
			parameters.put("H", 8.);

			ContextArithmetic context = new ContextArithmetic(parameters);

			double result = expression.evaluate(context);

			LOG.info("Result:     " + result);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
