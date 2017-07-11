package kkr.ktm.domains.tests.errors;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.tests.data.Test;

public class TestException extends BaseException {

	private Test test;
	private String parameter;

	public TestException(Test test, String parameter, String message) {
		super(message);
		this.test = test;
		this.parameter = parameter;
	}

	public TestException(Test test, String message) {
		super(message);
		this.test = test;
	}

	public Test getTest() {
		return test;
	}

	public String getParameter() {
		return parameter;
	}

	public String getMessage() {
		return (test != null ? test.toString() : "[?] ") //
				+ (parameter != null ? " - '" + parameter + "'" : "") //
				+ " - " + (super.getMessage() != null ? super.getMessage() : "");
	}
}
