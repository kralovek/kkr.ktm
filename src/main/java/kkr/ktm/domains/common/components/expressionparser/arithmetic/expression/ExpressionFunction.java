package kkr.ktm.domains.common.components.expressionparser.arithmetic.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import kkr.ktm.domains.common.components.expressionparser.Context;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.expressionparser.arithmetic.error.EvaluateExpressionException;

public class ExpressionFunction implements Expression {
	private String name;
	Method function;
	private Expression expression;
	private static Map<String, Method> methods = prepareMethods();

	private static Map<String, Method> prepareMethods() {
		Map<String, Method> methods = new HashMap<String, Method>();
		Method[] mathMethods = Math.class.getMethods();
		for (Method method : mathMethods) {
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
			if (!method.getParameterTypes()[0].equals(double.class)) {
				continue;
			}
			methods.put(method.getName(), method);
		}
		return methods;
	}

	public ExpressionFunction(String name, Expression expression) throws EvaluateExpressionException {
		this.name = name;
		this.expression = expression;
		function = methods.get(name);
		if (function == null) {
			throw new EvaluateExpressionException("Unsupported function: " + name);
		}
	}

	public double evaluate(Context context) throws EvaluateExpressionException {
		double argument = expression.evaluate(context);
		try {
			double value = (Double) function.invoke(null, argument);
			return value;
		} catch (Exception ex) {
			throw new EvaluateExpressionException("Cannot call function " + name + " with argument: " + argument);
		}
	}

	public String toString() {
		return name + "(" + expression.toString() + ")";
	}
}
