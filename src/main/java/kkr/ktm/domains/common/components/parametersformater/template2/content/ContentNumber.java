package kkr.ktm.domains.common.components.parametersformater.template2.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.ExpressionEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.format.Format;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatBase;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatType;
import kkr.ktm.domains.common.components.parametersformater.template.parts.TagNumber;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueDecimal;
import kkr.ktm.domains.common.components.parametersformater.template2.Position;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template2.part.TagType;

public class ContentNumber extends ContentBase implements Content {
	private static final Logger LOG = Logger.getLogger(ContentNumber.class);
	public static final TagType TAG = TagType.NUMBER;
	public static final String ATTR_FORMAT = "FORMAT";
	public static final String ATTR_EXPRESSION = "EXPRESSION";

	protected Format format;
	protected Expression expression;
	protected ExpressionParser expressionParser;

	public ContentNumber(Position position, Map<String, String> attributes, ExpressionParser expressionParser)
			throws ContentParseException, BaseException {
		super(position);
		LOG.trace("BEGIN");
		try {

			this.expressionParser = expressionParser;

			Map<String, String> attributesLocal = new HashMap<String, String>(attributes);

			//
			// FORMAT
			//
			if (attributes.containsKey(TagNumber.ATTR_FORMAT)) {
				String attributeValue = attributesLocal.remove(TagNumber.ATTR_FORMAT);
				try {
					format = FormatBase.newFormat(FormatType.VALUE, attributeValue);
					format.format(new ValueDecimal(0));
				} catch (Exception ex) {
					throw new ContentParseException(position, "" //
							+ "Bad format of the attribute " //
							+ "[" + TAG + " " + TagNumber.ATTR_FORMAT + "=" + "\"" + attributeValue + "\"]" //
							+ ". Problem: " + ex.getMessage());
				}
			} else {
				format = FormatBase.newFormat(FormatType.AUTO, null);
			}

			//
			// EXPRESSION
			//
			if (attributes.containsKey(TagNumber.ATTR_EXPRESSION)) {
				String attributeValue = attributesLocal.remove(TagNumber.ATTR_EXPRESSION);

				if (expressionParser == null) {
					throw new ContentParseException(position, "" //
							+ "Expression parser must be configured " //
							+ "[" + TAG + " " + TagNumber.ATTR_EXPRESSION + "=" + "\"" + attributeValue + "\"]");
				}

				expression = expressionParser.parseExpression(attributeValue);
			} else {
				throw new ContentParseException(position,
						"Missing attribute [" + TagNumber.TAG + " " + TagNumber.ATTR_EXPRESSION + "]");
			}

			checkUnknownAttributes(TAG, attributesLocal);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public String evaluate(ContextContent context) throws ContentEvaluateException {
		try {
			Number expressionValue = expression.evaluate(context.getContextExpression());
			Value value = new ValueDecimal(expressionValue.longValue());
			String formatedValue = format.format(value);
			return formatedValue;
		} catch (ExpressionEvaluateException ex) {
			throw new ContentEvaluateException(position,
					"Cannot evaluate expression of teh tag [" + TAG + "]: " + expression.toString(), ex);
		} catch (IllegalArgumentException ex) {
			throw new ContentEvaluateException(position,
					"Bad format string for a decimal numeric value [" + TAG + "]: " + format.toString());
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[').append(TAG);
		if (expression != null) {
			buffer.append(" ").append(toStringAttribute(ATTR_EXPRESSION, expression.toString()));
		}

		String formatValue = format.toString();
		if (formatValue != null && !formatValue.isEmpty()) {
			buffer.append(" ").append(toStringAttribute(ATTR_FORMAT, formatValue));
		}
		buffer.append(']');
		return buffer.toString();
	}
}
