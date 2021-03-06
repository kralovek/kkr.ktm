package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;
import kkr.ktm.domains.common.components.parametersformater.template.value.ComparatorValue;
import kkr.ktm.utils.parser.Position;

public class ContentIf extends ContentTagBase implements Content, Open {
	private static final Logger LOG = Logger.getLogger(ContentIf.class);

	private static final ComparatorValue COMPARATOR_VALUES = new ComparatorValue();

	public static final String ATTR_EXPRESSION1 = "EXPRESSION1";
	public static final String ATTR_EXPRESSION2 = "EXPRESSION2";
	public static final String ATTR_OPERATOR = "OPERATOR";

	public static enum OperatorType {
		BILATERAL, UNILATERAL
	}

	public static enum Operator {
		EQ, NE, LT, LE, GT, GE, EMPTY, NEMPTY
	}

	private Expression expression1;
	private Expression expression2;
	private Operator operator;
	private OperatorType operatorType;

	private Content contentTrue;
	private Content contentFalse;

	public ContentIf(Position position, Map<String, String> attributes, ExpressionParser expressionParser)
			throws ContentParseException {
		super(position, TagType.IF);
		LOG.trace("BEGIN");
		try {
			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// OPERATOR
			//
			operator = attributeEnum(true, ATTR_OPERATOR, attributesLocal.remove(ATTR_OPERATOR), Operator.values());
			operatorType = operatorType(operator);

			//
			// EXPRESSION1
			//
			expression1 = attributeExpression(true, ATTR_EXPRESSION1, attributesLocal.remove(ATTR_EXPRESSION1),
					expressionParser);

			//
			// EXPRESSION2
			//
			expression2 = attributeExpression(operatorType == OperatorType.BILATERAL, ATTR_EXPRESSION2,
					attributesLocal.remove(ATTR_EXPRESSION2), expressionParser);

			if (expression2 != null && operatorType != OperatorType.BILATERAL) {
				throw new ContentParseException(position,
						"Attribute [" + TAG + " " + ATTR_EXPRESSION2 + "] may not be used with UNILATERAL operator");
			}

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private OperatorType operatorType(Operator operator) {
		switch (operator) {
		case EQ:
		case NE:
		case LT:
		case LE:
		case GT:
		case GE:
			return OperatorType.BILATERAL;
		default:
			return OperatorType.UNILATERAL;
		}
	}

	public void addContent(Content content) throws ContentParseException {
		if (contentTrue == null) {
			contentTrue = content;
		} else if (contentFalse == null) {
			contentFalse = content;
		} else {
			throw new ContentParseException(position, "Contents true/false were already added to [" + TAG + "]");
		}
	}

	public void validate(Context context) throws ContentParseException {
		if (contentTrue != null) {
			contentTrue.validate(context);
		}
		if (contentFalse != null) {
			contentFalse.validate(context);
		}
	}

	private boolean evaluateOperatorUnilateral(Context context) throws ContentEvaluateException {
		Object object;
		try {
			object = expression1.evaluate(context);
		} catch (Exception ex) {
			throw new ContentEvaluateException(position, "Cannot evaluate " + ATTR_EXPRESSION1 + ": "
					+ expression1.toString() + " Problem: " + ex.getMessage(), ex);
		}

		boolean condition = true;
		try {
			switch (operator) {
			case EMPTY:
				condition = evaluateIsEmpty(object);
				break;
			case NEMPTY:
				condition = !evaluateIsEmpty(object);
				break;
			default:
				throw new IllegalStateException(
						"Unsupported value of [" + TAG + " " + ATTR_OPERATOR + "]: " + operator);
			}
		} catch (IllegalArgumentException ex) {
			throw new ContentEvaluateException(position, "Cannot compare expressions. Problem: " + ex.getMessage(), ex);
		}
		return condition;
	}

	private boolean evaluateOperatorBilateral(Context context) throws ContentEvaluateException {
		Object object1;
		Object object2;
		try {
			object1 = expression1.evaluate(context);
		} catch (Exception ex) {
			throw new ContentEvaluateException(position, "Cannot evaluate " + ATTR_EXPRESSION1 + ": "
					+ expression1.toString() + " Problem: " + ex.getMessage(), ex);
		}
		try {
			object2 = expression2.evaluate(context);
		} catch (Exception ex) {
			throw new ContentEvaluateException(position, "Cannot evaluate " + ATTR_EXPRESSION2 + ": "
					+ expression2.toString() + " Problem: " + ex.getMessage(), ex);
		}

		boolean condition = true;
		try {
			switch (operator) {
			case EQ:
				condition = COMPARATOR_VALUES.compare(object1, object2) == 0;
				break;
			case NE:
				condition = COMPARATOR_VALUES.compare(object1, object2) != 0;
				break;
			case LT:
				condition = COMPARATOR_VALUES.compare(object1, object2) < 0;
				break;
			case LE:
				condition = COMPARATOR_VALUES.compare(object1, object2) <= 0;
				break;
			case GT:
				condition = COMPARATOR_VALUES.compare(object1, object2) > 0;
				break;
			case GE:
				condition = COMPARATOR_VALUES.compare(object1, object2) >= 0;
				break;
			default:
				throw new IllegalStateException(
						"Unsupported value of [" + TAG + " " + ATTR_OPERATOR + "]: " + operator);
			}
		} catch (IllegalArgumentException ex) {
			throw new ContentEvaluateException(position, "Cannot compare expressions. Problem: " + ex.getMessage(), ex);
		}
		return condition;
	}

	public String evaluate(Context context) throws ContentEvaluateException {
		boolean condition;

		switch (operatorType) {
		case UNILATERAL:
			condition = evaluateOperatorUnilateral(context);
			break;

		case BILATERAL:
			condition = evaluateOperatorBilateral(context);
			break;

		default:
			throw new IllegalStateException("Unsupported operatorType: " + operatorType);
		}

		if (condition) {
			if (contentTrue != null) {
				return contentTrue.evaluate(context);
			} else {
				return "";
			}
		} else {
			if (contentFalse != null) {
				return contentFalse.evaluate(context);
			} else {
				return "";
			}
		}
	}

	private boolean evaluateIsEmpty(Object value) {
		return value == null || value instanceof String && ((String) value).isEmpty();
	}

	public String toString() {
		Map<String, String> attributes = new LinkedHashMap<String, String>();

		attributes.put(ATTR_OPERATOR, operator.toString());
		attributes.put(ATTR_EXPRESSION1, expression1.toString());
		if (expression2 != null) {
			attributes.put(ATTR_EXPRESSION2, expression2.toString());
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(toStringTag(attributes));

		if (contentTrue != null) {
			buffer.append(contentTrue.toString());
		}

		if (contentFalse != null) {
			buffer.append("[" + TagType.ELSE + "]");
			buffer.append(contentFalse.toString());
		}

		buffer.append("[" + TagType.END + "]");
		return buffer.toString();
	}
}
