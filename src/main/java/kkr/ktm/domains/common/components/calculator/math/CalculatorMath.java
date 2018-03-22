package kkr.ktm.domains.common.components.calculator.math;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.calculator.Calculator;
import kkr.ktm.domains.common.components.calculator.CalculatorException;

public class CalculatorMath extends CalculatorMathFwk implements Calculator {
	private static final Map<Integer, Map<String, Method>> METHODS = prepareMethods();

	private static Map<Integer, Map<String, Method>> prepareMethods() {
		Map<Integer, Map<String, Method>> methods = new HashMap<Integer, Map<String, Method>>();
		Method[] mathMethods = Math.class.getMethods();
		methods: for (Method method : mathMethods) {
			if (!Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			if (!method.getReturnType().equals(double.class)) {
				continue;
			}
			if (method.getParameterCount() != 1) {
				continue;
			}
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				if (!method.getParameterTypes()[i].equals(double.class)) {
					continue methods;
				}
			}
			Map<String, Method> countMethods = methods.get(method.getParameterTypes().length);
			if (countMethods == null) {
				countMethods = new HashMap<String, Method>();
				methods.put(method.getParameterTypes().length, countMethods);
			}
			countMethods.put(method.getName(), method);
		}
		return methods;
	}

	private Method findMethod(String function, Object... arguments) throws CalculatorException {
		int countArguments = arguments != null ? arguments.length : 0;
		Map<String, Method> countMethods = METHODS.get(countArguments);
		Method method;
		if (countMethods == null || (method = countMethods.get(function)) == null) {
			throw new CalculatorException(
					"Unsupported function: " + function + " with " + countArguments + " arguments");
		}
		return method;
	}

	public Object calculate(String function, Object... arguments) throws CalculatorException {
		Method method = findMethod(function, arguments);

		Number[] argumentNumbers;
		if (arguments != null && arguments.length != 0) {
			argumentNumbers = new Number[arguments.length];
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i] == null || !(arguments[i] instanceof Number)) {
					throw new CalculatorException("All arguments of Math function must be numbers: " + function + "("
							+ UtilsString.toStringArray(arguments, null, null, ",") + ")");
				}
				argumentNumbers[i] = (Number) arguments[i];
			}
		} else {
			argumentNumbers = new Number[0];
		}
		try {
			Number value = (Number) method.invoke(null, argumentNumbers);
			return value;
		} catch (Exception ex) {
			throw new CalculatorException("Cannot call function " + toString());
		}
	}
}
