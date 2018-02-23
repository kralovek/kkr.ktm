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
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.OperatorSeparator;

// 1^2 + 2*{A} + 3*{B}/{C} + (4*{A} + 5*7*{D}/(6 + {C})) + SIN(8)

public class ExpressionParserArithmetic extends ExpressionParserArithmeticFwk implements ExpressionParser {
	private static final Logger LOG = Logger.getLogger(ExpressionParserArithmetic.class);

	private static final Pattern PATTERN_NUMBER_START = Pattern.compile("^[0-9\\.].*");
	private static final Pattern PATTERN_NUMBER = Pattern.compile("^(0?|[1-9][0-9]*)(\\.[0-9]*)?$");
	private static final Pattern PATTERN_PARAMETER_START = Pattern.compile("^\\{.*");
	private static final Pattern PATTERN_PARAMETER = Pattern
			.compile("^\\{ *[a-z_A-Z][a-z_A-Z0-9]*( *\\[.*\\]| *)*\\}$");
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

	private Character getCloseParenthese(char openParenthese) {
		switch (openParenthese) {
		case '(':
			return ')';
		case '{':
			return '}';
		case '[':
			return ']';
		default:
			return null;
		}
	}

	private Collection<Addition> parseAdditions(Position position, String text, Operator operatorType)
			throws ParseExpressionException {
		LOG.trace("BEGIN: {" + operatorType.getClass().getSimpleName() + "} [" + position.getPosition() + "] '" + text
				+ "'");
		try {
			Collection<Addition> retval = new ArrayList<Addition>();
			char[] chars = text.toCharArray();

			Character parentheseOpen = null;
			Character parentheseClose = null;
			int parentheseLevel = 0;
			int iPosOpen = 0;
			Addition cutCurrent = null;

			int iPos = 0;
			for (; iPos < chars.length; iPos++) {
				char c = chars[iPos];

				if (parentheseLevel == 0 && (parentheseClose = getCloseParenthese(c)) != null) {
					parentheseOpen = c;
				}

				if (parentheseOpen != null && c == parentheseOpen) {
					if (parentheseLevel == 0) {
						iPosOpen = iPos;
					}
					parentheseLevel++;

					if (cutCurrent == null) {
						cutCurrent = new Addition(position.movePosition(iPos), null);
						retval.add(cutCurrent);
					}

					continue;
				}
				if (parentheseClose != null && c == parentheseClose) {
					parentheseLevel--;
					if (parentheseLevel == 0) {
						parentheseOpen = null;
						parentheseClose = null;
					} else if (parentheseLevel < 0) {
						throw new ParseExpressionException(position.movePosition(iPos), "Missing opening parenthese",
								text);
					}
					continue;
				}

				if (parentheseLevel != 0) {
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

			if (parentheseLevel != 0) {
				throw new ParseExpressionException(position.movePosition(iPosOpen), "Missing closing parenthese", text);
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
				throw new ParseExpressionException(position, "Literal is not a parenthese closed expression", text);
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
				throw new ParseExpressionException(position, "Literal is not a number", text);
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
				throw new ParseExpressionException(position, "Literal is not a function", text);
			}

			int iParentBegin = text.indexOf('(');
			int iParentEnd = text.lastIndexOf(')');

			String name = text.substring(0, iParentBegin).trim();
			String arguments = text.substring(iParentBegin + 1, iParentEnd);

			Position positionArgument = position.movePosition(iParentBegin + 1);

			Collection<Addition> additions = parseAdditions(positionArgument, arguments, OperatorSeparator.COMMA);
			Collection<Expression> expressions = new ArrayList<Expression>();
			for (Addition argument : additions) {
				Expression expression = parseOperator(argument.getPosition(), argument.getText(), LEVEL);
				expressions.add(expression);
			}

			try {
				ExpressionFunction expressionFunction = new ExpressionFunction(name,
						expressions.toArray(new Expression[expressions.size()]));
				LOG.trace("OK");
				return expressionFunction;
			} catch (Exception ex) {
				throw new ParseExpressionException(position, "Cannot evalueate function", name, ex);
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
				throw new ParseExpressionException(position, "Literal is not a parameter", text);
			}
			String content = text.substring(1, text.length() - 1);

			int iPos = content.indexOf('[');

			String name;

			if (iPos == -1) {
				name = content.trim();
				ExpressionParameter expressionParameter = new ExpressionParameter(name);
				LOG.trace("OK");
				return expressionParameter;
			} else {
				name = content.substring(0, iPos).trim();
			}

			Position positionIndexes = position.movePosition(iPos + 1);

			String contentIndexes = content.substring(iPos).trim();
			char[] chars = contentIndexes.toCharArray();

			Collection<Expression> expressions = new ArrayList<Expression>();

			int iOpenLevel = 1;
			int iPosOpen = 0;
			for (iPos = 1; iPos < chars.length; iPos++) {
				char c = chars[iPos];
				switch (c) {
				case ']':
					iOpenLevel--;
					if (iOpenLevel == 0) {
						String contentIndex = contentIndexes.substring(iPosOpen + 1, iPos);
						Expression expression = parseOperator(positionIndexes.movePosition(iPosOpen + 1), contentIndex,
								LEVEL);
						expressions.add(expression);
					} else if (iOpenLevel < 0) {
						throw new ParseExpressionException(positionIndexes.movePosition(iPosOpen),
								"Not matched closing bracket ']", contentIndexes.substring(iPosOpen, iPos + 1));
					}
					break;
				case '[':
					if (iOpenLevel == 0) {
						iPosOpen = iPos;
					}
					iOpenLevel++;
					break;
				default:
					if (iOpenLevel == 0 && !isSpace(c)) {
						throw new ParseExpressionException(positionIndexes.movePosition(iPosOpen),
								"Not allowed character between ']' and '['", contentIndexes);
					}
				}
			}

			if (iOpenLevel != 0) {
				throw new ParseExpressionException(positionIndexes.movePosition(iPosOpen), "Missing closing bracket",
						contentIndexes);
			}

			ExpressionParameter expressionParameter = new ExpressionParameter(name,
					expressions.toArray(new Expression[expressions.size()]));
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
				throw new ParseExpressionException(position, "Missing a literal", text);
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

			throw new ParseExpressionException(position, "Unknown literal", text);

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
					throw new ParseExpressionException(position, "Missing expression", text);
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
		try {
			Expression retval = parseOperator(position, text, LEVEL);
			return retval;
		} catch (ParseExpressionException ex) {
			ex.setExpression(text);
			throw ex;
		}
	}

	public static final void main(String[] argv) throws BaseException {
		LOG.trace("BEGIN");
		try {
			// String text = "2^({A} + 2) + 2.5*{B} + 3*{C}/{D} + (4*{E} + 5*7*{F}/(6 +
			// {G})) + sin({H}) + {PAR[1][{PAR2}][1 + 2*sin(8)]}";
			String text = "1 + {PAR[1][{PAR2}][1 + 2*sin(8)]}";

			LOG.info("Text: " + text);

			ExpressionParserArithmetic parser = new ExpressionParserArithmetic();
			parser.config();
			Expression expression = parser.parseExpression(text);

			LOG.info("Expression: " + text);
			LOG.info("Formated:   " + expression.toString());

			Map<String, Object> parameters = new HashMap<String, Object>();

			parameters.put("A", 1.);
			parameters.put("B", 2.);
			parameters.put("C", 3.);
			parameters.put("D", 4.);
			parameters.put("E", 5.);
			parameters.put("F", 6.);
			parameters.put("G", 7.);
			parameters.put("H", 8.);

			ContextArithmetic context = new ContextArithmetic();
			context.addParameters(parameters);

			Number result = expression.evaluate(context);

			LOG.info("Result:     " + result);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
