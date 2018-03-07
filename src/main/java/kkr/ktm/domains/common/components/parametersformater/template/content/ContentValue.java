package kkr.ktm.domains.common.components.parametersformater.template.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionEvaluateException;
import kkr.ktm.domains.common.components.formatter.Formatter;
import kkr.ktm.domains.common.components.formatter.FormatterException;
import kkr.ktm.domains.common.components.formatter.bytype.FormatterFactoryByType;
import kkr.ktm.domains.common.components.parametersformater.template.Position;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template.part.TagType;

public class ContentValue extends ContentTagBase implements Content {
	private static final Logger LOG = Logger.getLogger(ContentValue.class);
	public static final String ATTR_EXPRESSION = "EXPRESSION";

	public static final String ATTR_FORMAT_DATE = "FORMAT-DATE";
	public static final String ATTR_FORMAT_DECIMAL = "FORMAT-DECIMAL";
	public static final String ATTR_FORMAT_INTEGER = "FORMAT-INTEGER";
	public static final String ATTR_FORMAT_STRING = "FORMAT-STRING";
	public static final String ATTR_FORMAT_BOOLEAN = "FORMAT-BOOLEAN";

	protected Formatter formatter;
	protected Expression expression;
	protected ExpressionParser expressionParser;

	public ContentValue(Position position, Map<String, String> attributes, FormatterFactoryByType formatterFactory,
			ExpressionParser expressionParser) throws ContentParseException, BaseException {
		super(position, TagType.VALUE);
		LOG.trace("BEGIN");
		try {

			this.expressionParser = expressionParser;

			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// FORMATTER
			//
			formatter = attributeFormat( //
					attributesLocal.remove(ATTR_FORMAT_STRING), //
					attributesLocal.remove(ATTR_FORMAT_BOOLEAN), //
					attributesLocal.remove(ATTR_FORMAT_INTEGER), //
					attributesLocal.remove(ATTR_FORMAT_DECIMAL), //
					attributesLocal.remove(ATTR_FORMAT_DATE), //
					formatterFactory);

			//
			// EXPRESSION
			//
			expression = attributeExpression(true, ATTR_EXPRESSION, attributesLocal.remove(ATTR_EXPRESSION),
					expressionParser);

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void validate(Context context) throws ContentParseException {
		// OK
	}

	public String evaluate(Context context) throws ContentEvaluateException {
		Object expressionValue = null;
		try {
			expressionValue = expression.evaluate(context);
			String formatedValue = formatter.format(expressionValue);
			return formatedValue;
		} catch (ExpressionEvaluateException ex) {
			throw new ContentEvaluateException(position,
					"Cannot evaluate expression of teh tag [" + TAG + "]: " + expression.toString(), ex);
		} catch (FormatterException ex) {
			throw new ContentEvaluateException(position,
					"[" + TAG + "]: Cannot format the value " + expressionValue + " Problem: " + ex.getMessage(), ex);
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[').append(TAG);
		if (expression != null) {
			buffer.append(" ").append(toStringAttribute(ATTR_EXPRESSION, expression.toString()));
		}

		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_STRING, "..."));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_BOOLEAN, "..."));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_INTEGER, "..."));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_DECIMAL, "..."));
		buffer.append(" ").append(toStringAttribute(ATTR_FORMAT_DATE, "..."));
		buffer.append(']');
		return buffer.toString();
	}
}
