package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.EvaluateExpressionException;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.operator.OperatorSeparator;

public class ExpressionFunction implements Expression {
	private String name;
	Method function;
	private Expression[] argumentExpressions;
	private static Map<Integer, Map<String, Method>> methods = prepareMethods();

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

	public ExpressionFunction(String name, Expression[] argumentExpressions) throws EvaluateExpressionException {
		this.name = name;
		this.argumentExpressions = argumentExpressions;
		Map<String, Method> countMethods = methods.get(argumentExpressions.length);
		if (countMethods == null || (function = countMethods.get(name)) == null) {
			throw new EvaluateExpressionException(
					"Unsupported function: " + name + " with " + argumentExpressions.length + " arguments");
		}
	}

	public Number evaluate(Context context) throws EvaluateExpressionException {
		Object[] argumentValues = new Object[argumentExpressions.length];
		for (int i = 0; i < argumentValues.length; i++) {
			argumentValues[i] = argumentExpressions[i].evaluate(context);
		}
		try {
			double value = (Double) function.invoke(null, argumentValues);
			return value;
		} catch (Exception ex) {
			throw new EvaluateExpressionException("Cannot call function " + toString());
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < argumentExpressions.length; i++) {
			if (buffer.length() != 0) {
				buffer.append(OperatorSeparator.COMMA.getSymbol());
			}
			argumentExpressions.toString();
		}
		return name + "(" + buffer.toString() + ")";
	}
}
