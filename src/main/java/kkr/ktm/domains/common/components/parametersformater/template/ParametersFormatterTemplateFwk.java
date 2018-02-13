package kkr.ktm.domains.common.components.parametersformater.template;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;

public abstract class ParametersFormatterTemplateFwk {
	private boolean configured;

	protected ExpressionParser expressionParser;

	public void config() throws BaseException {
		configured = false;
		if (expressionParser == null) {
			// OK
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[").append(this.getClass().getName()).append("]\n");
		return buffer.toString();
	}
}
