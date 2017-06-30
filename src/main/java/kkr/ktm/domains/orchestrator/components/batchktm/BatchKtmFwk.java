package kkr.ktm.domains.orchestrator.components.batchktm;

import kkr.ktm.domains.common.components.trafficlights.TrafficLights;
import kkr.ktm.domains.orchestrator.components.testlancer.TestLancer;
import kkr.ktm.domains.tests.components.testloader.TestLoader;
import kkr.ktm.domains.tests.components.testreporter.TestReporter;
import kkr.common.errors.ConfigurationException;

public abstract class BatchKtmFwk {
	private boolean configured;

	protected TestLoader testLoader;
	protected TestReporter testReporter;
	protected TrafficLights trafficLights;
	protected String sysPrefix;

	protected TestLancer testLancer;

	public void config() throws ConfigurationException {
		configured = false;
		if (testLoader == null) {
			throw new ConfigurationException("Parameter 'testLoader' is not configured");
		}
		if (testReporter == null) {
			throw new ConfigurationException("Parameter 'testReporter' is not configured");
		}
		if (trafficLights == null) {
			throw new ConfigurationException("Parameter 'trafficLights' is not configured");
		}
		if (sysPrefix == null) {
			throw new ConfigurationException("Parameter 'sysPrefix' is not configured");
		}
		if (testLancer == null) {
			throw new ConfigurationException("Parameter 'testLancer' is not configured");
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

	public TrafficLights getTrafficLights() {
		return trafficLights;
	}

	public void setTrafficLights(TrafficLights trafficLights) {
		this.trafficLights = trafficLights;
	}

	public String getSysPrefix() {
		return sysPrefix;
	}

	public void setSysPrefix(String sysPrefix) {
		this.sysPrefix = sysPrefix;
	}

	public TestLancer getTestLancer() {
		return testLancer;
	}

	public void setTestLancer(TestLancer testLancer) {
		this.testLancer = testLancer;
	}
}
