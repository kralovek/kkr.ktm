package kkr.ktm.domains.common.components.parametersformater.template.parts;

import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.parametersformater.template.format.Format;

public class TagNumber implements Tag {
	public static final String TAG = "NUMBER";
	public static final String ATTR_FORMAT = "FORMAT";
	public static final String ATTR_EXPRESSION = "EXPRESSION";

	protected Format format;
	protected Expression expression;

	public String getTagName() {
		return TAG;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

	public static String getSyntax() {
		return "[" + TAG //
				+ " " + ATTR_FORMAT + "=" + "\"Pattern\"" //
				+ " " + ATTR_EXPRESSION + "=" + "\"Expression\"" //
				+ "]";
	}

	public String toString() {
		return "[" + TAG //
				+ (format != null ? " " + ATTR_FORMAT + "=\"" + format.toString() + "\"" : "") //
				+ (expression != null ? " " + ATTR_EXPRESSION + "=\"" + expression.toString() + "\"" : "") //
				+ "]";
	}
}
