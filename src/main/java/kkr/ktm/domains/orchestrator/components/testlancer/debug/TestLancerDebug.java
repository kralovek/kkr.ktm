package kkr.ktm.domains.orchestrator.components.testlancer.debug;
import org.apache.log4j.Logger;

import kkr.ktm.domains.orchestrator.components.testlancer.TestLancer;
import kkr.ktm.domains.orchestrator.data.TestOutputImpl;
import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.common.errors.BaseException;

public class TestLancerDebug extends TestLancerDebugFwk implements TestLancer {
	private static final Logger LOG = Logger.getLogger(TestLancerDebug.class);

	public TestOutput lanceTest(TestInput testInput) throws BaseException {
		LOG.trace("BEGIN");
		try {
			TestOutput testOutput = new TestOutputImpl(testInput.getName(), testInput.getDescription(), testInput.getSource(), testInput.getType(),
					testInput.getCode(), null);

			if ("1".equals(testInput.getCode())) {
				testOutput.getDataOutput().put("VAL1", "v11");
				testOutput.getDataOutput().put("VAL2", "v12");
				testOutput.getDataOutput().put("VAL3", "v13");
				testOutput.getDataOutput().put("VAL4", "v14");
				testOutput.getDataOutput().put("VAL5", "v15");
			}
			if ("2".equals(testInput.getCode())) {
				testOutput.getDataOutput().put("VAL1", "v21");
				testOutput.getDataOutput().put("VAL2", "x");
				testOutput.getDataOutput().put("VAL3", "v23");
				testOutput.getDataOutput().put("VAL4", "x");
				testOutput.getDataOutput().put("VAL5", "v25");
			}
			if ("3".equals(testInput.getCode())) {
				testOutput.getDataOutput().put("VAL1", "x");
				testOutput.getDataOutput().put("VAL2", "x");
				testOutput.getDataOutput().put("VAL3", "x");
				testOutput.getDataOutput().put("VAL4", "v34");
				testOutput.getDataOutput().put("VAL5", "v3x");
			}
			if ("4".equals(testInput.getCode())) {
				testOutput.getDataOutput().put("VAL1", "v41");
				testOutput.getDataOutput().put("VAL2", "v42");
				testOutput.getDataOutput().put("VAL3", "v43");
				testOutput.getDataOutput().put("VAL4", "v44");
				testOutput.getDataOutput().put("VAL5", "v45");
			}
			if ("5".equals(testInput.getCode())) {
				testOutput.getDataOutput().put("VAL1", "v51");
				testOutput.getDataOutput().put("VAL2", "v52");
				testOutput.getDataOutput().put("VAL3", "v53");
				testOutput.getDataOutput().put("VAL4", "v54");
				testOutput.getDataOutput().put("VAL5", "v55");
			}

			LOG.trace("OK");
			return testOutput;
		} finally {
			LOG.trace("END");
		}
	}
}
