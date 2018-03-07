package kkr.ktm.domains.common.components.calculator.byprefix;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.calculator.Calculator;
import kkr.ktm.domains.common.components.calculator.CalculatorException;

public class CalculatorByPrefix extends CalculatorByPrefixFwk implements Calculator {
	private static final Logger LOG = Logger.getLogger(CalculatorByPrefix.class);

	public Object calculate(String function, Object... arguments) throws CalculatorException {
		LOG.trace("BEGIN");
		try {
			String[] parts = function.split("\\.");

			if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
				throw new CalculatorException("Function name must be prefixed (prefix.name): " + function);
			}

			String prefix = parts[0];
			String name = function.substring(prefix.length() + 1);

			Calculator calculator = calculators.get(prefix);

			if (calculator == null) {
				throw new CalculatorException("No calculator is configured for the function prefix: " + prefix);
			}

			Object retval = calculator.calculate(name, arguments);

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
