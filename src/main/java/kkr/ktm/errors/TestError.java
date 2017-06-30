package kkr.ktm.errors;

public class TestError extends RuntimeException {

	public TestError() {
		super();
	}

	public TestError(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public TestError(String paramString) {
		super(paramString);
	}

	public TestError(Throwable paramThrowable) {
		super(paramThrowable);
	}

}
