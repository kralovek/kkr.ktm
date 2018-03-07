package kkr.ktm.domains.common.components.calculator.byprefix;

import java.util.Map;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.calculator.Calculator;

public abstract class CalculatorByPrefixFwk {
	private boolean configured;

	protected Map<String, Calculator> calculators;

	public void config() throws ConfigurationException {
		configured = false;
		if (calculators == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'calculators' is empty");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Map<String, Calculator> getCalculators() {
		return calculators;
	}

	public void setCalculators(Map<String, Calculator> calculators) {
		this.calculators = calculators;
	}
}
