package kkr.ktm.domains.orchestrator.components.batchdev;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;

public abstract class BatchDevFwk {
	private boolean configured;

	protected ExpressionParser expressionParser;

	public void config() throws ConfigurationException {
		configured = false;
		if (expressionParser == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'expressionParser' is not configured");
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
}
