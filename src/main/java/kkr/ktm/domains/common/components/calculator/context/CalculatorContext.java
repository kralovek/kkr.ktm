package kkr.ktm.domains.common.components.calculator.context;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.calculator.Calculator;
import kkr.ktm.domains.common.components.calculator.CalculatorException;

public class CalculatorContext extends CalculatorContextFwk implements Calculator {
	private static final Logger LOG = Logger.getLogger(CalculatorContext.class);

	private static final String FUNCTION_LENGTH = "LENGTH";

	private int calculateLENGTH(Object... arguments) throws CalculatorException {
		if (arguments == null || arguments.length != 1) {
			throw new CalculatorException("Function " + FUNCTION_LENGTH + " expects exactely 1 argument, but it has: "
					+ (arguments != null ? arguments.length : 0));
		}

		int retval;
		if (arguments[0] != null && !arguments[0].getClass().isArray()) {
			retval = 1;
		} else {
			Object[] array = (Object[]) arguments[0];
			retval = array.length != 0 ? array.length : 1;
		}
		return retval;
	}

	public Object calculate(String function, Object... arguments) throws CalculatorException {
		LOG.trace("BEGIN");
		try {
			if (FUNCTION_LENGTH.equals(function)) {
				return calculateLENGTH(arguments);
			} else {
				throw new CalculatorException("Unsupported function: " + function);
			}
		} finally {
			LOG.trace("END");
		}
	}
}
