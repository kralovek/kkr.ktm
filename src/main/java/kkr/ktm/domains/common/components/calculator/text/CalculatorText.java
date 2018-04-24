package kkr.ktm.domains.common.components.calculator.text;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.calculator.Calculator;
import kkr.ktm.domains.common.components.calculator.CalculatorException;
import kkr.ktm.domains.common.components.formatter.FormatterException;

public class CalculatorText extends CalculatorTextFwk implements Calculator {
	private static final Logger LOG = Logger.getLogger(CalculatorText.class);

	private static enum Function {
		LENGTH, CONCAT, TOTEXT
	}

	private int calculateLENGTH(Object... arguments) throws CalculatorException {
		if (arguments == null || arguments.length != 1) {
			throw new CalculatorException("Function " + Function.LENGTH + " expects exactely 1 argument, but it has: "
					+ (arguments != null ? arguments.length : 0));
		}

		if (arguments[0] == null) {
			return 0;
		}

		if (!(arguments[0] instanceof String)) {
			throw new CalculatorException("Function " + Function.LENGTH
					+ " expects argument of type String but it received: " + (String) arguments[0]);
		}

		int retval = ((String) arguments[0]).length();
		return retval;
	}

	private String calculateCONCAT(Object... arguments) throws CalculatorException {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arguments.length; i++) {
			String string = toString(arguments[i]);
			buffer.append(string);
		}
		return buffer.toString();
	}

	private String calculateTOTEXT(Object... arguments) throws CalculatorException {
		if (arguments == null || arguments.length != 1) {
			throw new CalculatorException("Function " + Function.TOTEXT + " expects exactely 1 argument, but it has: "
					+ (arguments != null ? arguments.length : 0));
		}
		String retval = toString(arguments[0]);
		return retval;
	}

	private String toString(Object object) throws CalculatorException {
		try {
			String retval = formatter.format(object);
			return retval;
		} catch (FormatterException ex) {
			throw new CalculatorException("Function " + Function.TOTEXT + " cannot format the value: " + object, ex);
		}
	}

	public Object calculate(String functionName, Object... arguments) throws CalculatorException {
		LOG.trace("BEGIN");
		try {
			Object retval;
			try {
				Function function = Function.valueOf(functionName);
				switch (function) {
				case LENGTH:
					retval = calculateLENGTH(arguments);
					break;

				case CONCAT:
					retval = calculateCONCAT(arguments);
					break;

				case TOTEXT:
					retval = calculateTOTEXT(arguments);
					break;

				default:
					throw new CalculatorException("Unsupported function: " + function);
				}
			} catch (CalculatorException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new CalculatorException("Unknown function: " + functionName);
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

}
