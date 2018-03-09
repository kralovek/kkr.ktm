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

	public static enum Operator {
		EQ, NE, LT, LE, GT, GE
	}

	private Expression expression1;
	private Expression expression2;
	private Operator operator;

	private Content contentTrue;
	private Content contentFalse;

	public ContentIf(Position position, Map<String, String> attributes, ExpressionParser expressionParser)
			throws ContentParseException {
		super(position, TagType.IF);
		LOG.trace("BEGIN");
		try {
			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// EXPRESSION1
			//
			expression1 = attributeExpression(true, ATTR_EXPRESSION1, attributesLocal.remove(ATTR_EXPRESSION1),
					expressionParser);

			//
			// EXPRESSION2
			//
			expression2 = attributeExpression(true, ATTR_EXPRESSION2, attributesLocal.remove(ATTR_EXPRESSION2),
					expressionParser);

			//
			// OPERATOR
			//
			operator = attributeEnum(true, ATTR_OPERATOR, attributesLocal.remove(ATTR_OPERATOR), Operator.values());

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
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

	public String evaluate(Context context) throws ContentEvaluateException {
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

	public String toString() {
		Map<String, String> attributes = new LinkedHashMap<String, String>();

		attributes.put(ATTR_OPERATOR, operator.toString());
		attributes.put(ATTR_EXPRESSION1, expression1.toString());
		attributes.put(ATTR_EXPRESSION2, expression2.toString());

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
