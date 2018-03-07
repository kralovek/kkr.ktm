package kkr.ktm.domains.common.components.calculator;

public interface Calculator {

	Object calculate(String function, Object... arguments) throws CalculatorException;
}
