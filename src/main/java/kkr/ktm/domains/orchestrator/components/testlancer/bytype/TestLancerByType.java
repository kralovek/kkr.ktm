package kkr.ktm.domains.orchestrator.components.testlancer.bytype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import kkr.ktm.domains.orchestrator.components.testlancer.TestLancer;
import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.ktm.exception.BaseException;

public class TestLancerByType extends TestLancerByTypeFwk implements TestLancer {

	public TestOutput lanceTest(TestInput testInput) throws BaseException {
		if (testInput == null) {
			throw new IllegalArgumentException("TestInput is null");
		}

		Map<Pattern, TestLancer> matched = new LinkedHashMap<Pattern, TestLancer>();

		for (Map.Entry<Pattern, TestLancer> entry : testLancersByType.entrySet()) {
			if (entry.getKey().matcher(testInput.getType()).matches()) {
				if (matched.containsKey(entry.getKey())) {
					throw new IllegalStateException("Type match more then one TestLancer: " + entry.getKey().pattern() + " and "
							+ matched.keySet().iterator().next().pattern());
				}
				matched.put(entry.getKey(), entry.getValue());
			}
		}

		TestLancer testLancer = matched.entrySet().iterator().next().getValue();

		TestOutput testOutput = testLancer.lanceTest(testInput);

		return testOutput;
	}

}
