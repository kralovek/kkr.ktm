package kkr.ktm.domains.common.components.expressionparser.generic;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.calculator.Calculator;

public abstract class ExpressionParserGenericFwk {
	private boolean configured;

	private static final char DEFAULT_QUOTE = '"';

	protected Calculator calculator;
	protected Character symbolQuote;

	public void config() throws ConfigurationException {
		configured = false;
		if (calculator == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'calculator' is not configured");
		}
		if (symbolQuote == null) {
			symbolQuote = DEFAULT_QUOTE;
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Calculator getCalculator() {
		return calculator;
	}

	public void setCalculator(Calculator calculator) {
		this.calculator = calculator;
	}

}
