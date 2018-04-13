package kkr.ktm.domains.common.components.calculator.text;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.calculator.Calculator;
import kkr.ktm.domains.common.components.calculator.CalculatorException;

public class CalculatorText extends CalculatorTextFwk implements Calculator {
	private static final Logger LOG = Logger.getLogger(CalculatorText.class);

	private static final String FUNCTION_LENGTH = "LENGTH";
	private static final String FUNCTION_CONCAT = "CONCAT";

	private int calculateLENGTH(Object... arguments) throws CalculatorException {
		if (arguments == null || arguments.length != 1) {
			throw new CalculatorException("Function " + FUNCTION_LENGTH + " expects exactely 1 argument, but it has: "
					+ (arguments != null ? arguments.length : 0));
		}

		if (arguments[0] == null) {
			return 0;
		}

		if (!(arguments[0] instanceof String)) {
			throw new CalculatorException("Function " + FUNCTION_LENGTH
					+ " expects argument of type String but it received: " + (String) arguments[0]);
		}

		int retval = ((String) arguments[0]).length();
		return retval;
	}

	private String calculateCONCAT(Object... arguments) throws CalculatorException {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arguments.length; i++) {
			if (!(arguments[i] instanceof String)) {
				throw new CalculatorException("Function " + FUNCTION_CONCAT
						+ " expects list of arguments of type String but received argument[" + i + "] of type: "
						+ (String) arguments[i]);
			}
			buffer.append(arguments[i]);
		}
		return buffer.toString();
	}

	public Object calculate(String function, Object... arguments) throws CalculatorException {
		LOG.trace("BEGIN");
		try {
			if (FUNCTION_LENGTH.equals(function)) {
				LOG.trace("OK");
				return calculateLENGTH(arguments);
			} else if (FUNCTION_CONCAT.equals(function)) {
				LOG.trace("OK");
				return calculateCONCAT(arguments);
			} else {
				throw new CalculatorException("Unsupported function: " + function);
			}
		} finally {
			LOG.trace("END");
		}
	}

}
