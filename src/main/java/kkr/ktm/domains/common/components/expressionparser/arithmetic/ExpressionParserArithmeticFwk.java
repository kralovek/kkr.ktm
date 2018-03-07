package kkr.ktm.domains.common.components.expressionparser.arithmetic;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.calculator.Calculator;

public abstract class ExpressionParserArithmeticFwk {
	private boolean configured;

	protected Calculator calculator;

	public void config() throws ConfigurationException {
		configured = false;
		if (calculator == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'calculator' is not configured");
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
