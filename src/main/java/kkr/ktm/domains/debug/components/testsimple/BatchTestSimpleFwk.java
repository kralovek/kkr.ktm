package kkr.ktm.domains.debug.components.testsimple;

import kkr.ktm.domains.tests.components.testloader.TestLoader;
import kkr.ktm.domains.tests.components.testreporter.TestReporter;
import kkr.ktm.exception.ConfigurationException;

public abstract class BatchTestSimpleFwk {
	private boolean configured;

	protected TestLoader testLoader;
	protected TestReporter testReporter;

	public void config() throws ConfigurationException {
		configured = false;
		if (testLoader == null) {
			throw new ConfigurationException("Parameter 'testLoader' is not configured");
		}
		if (testReporter == null) {
			throw new ConfigurationException("Parameter 'testReporter' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public TestLoader getTestLoader() {
		return testLoader;
	}

	public void setTestLoader(TestLoader testLoader) {
		this.testLoader = testLoader;
	}

	public TestReporter getTestReporter() {
		return testReporter;
	}

	public void setTestReporter(TestReporter testReporter) {
		this.testReporter = testReporter;
	}
}
