package kkr.ktm.domains.orchestrator.components.testlancer.bytype;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import kkr.ktm.domains.orchestrator.components.testlancer.TestLancer;
import kkr.ktm.exception.ConfigurationException;

public abstract class TestLancerByTypeFwk {
	private boolean configured;

	private Map<String, TestLancer> _testLancersByType;
	protected Map<Pattern, TestLancer> testLancersByType;

	public void config() throws ConfigurationException {
		configured = false;
		testLancersByType = new HashMap<Pattern, TestLancer>();
		if (_testLancersByType != null) {
			for (Map.Entry<String, TestLancer> entry : _testLancersByType.entrySet()) {
				int i = 0;
				try {
					Pattern pattern = Pattern.compile(entry.getKey());
					testLancersByType.put(pattern, entry.getValue());
					i++;
				} catch (Exception ex) {
					throw new ConfigurationException("Parameter 'testLancersByType[" + i + "]' has bad value of the key: " + entry.getKey());
				}
			}
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Map<String, TestLancer> getTestLancersByType() {
		return _testLancersByType;
	}

	public void setTestLancersByType(Map<String, TestLancer> testLancersByType) {
		this._testLancersByType = testLancersByType;
	}
}
