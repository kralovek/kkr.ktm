package kkr.ktm.batchs.ktmsimple;
import java.util.List;

import org.apache.log4j.Logger;

import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.exception.BaseException;

public class BatchKtmSimple extends BatchKtmSimpleFwk {
	private static final Logger LOG = Logger.getLogger(BatchKtmSimple.class);

	public void run() throws BaseException {
		LOG.trace("BEGIN");
		try {
			String batchId = "batchId";
			String source = "wrk/KTM.xlsx";

			List<TestInput> testsInput = null; //testLoader.loadTests(source);

			for (TestInput testInput : testsInput) {
				TestOutput testOutput = new TestOutputImpl(testInput.getSource(), testInput.getType(), testInput.getId());

				if ("1".equals(testInput.getId())) {
					testOutput.getDataOutput().put("VAL1", "v11");
					testOutput.getDataOutput().put("VAL2", "v12");
					testOutput.getDataOutput().put("VAL3", "v13");
				}
				if ("2".equals(testInput.getId())) {
					testOutput.getDataOutput().put("VAL1", "v21");
					testOutput.getDataOutput().put("VAL2", "x");
					testOutput.getDataOutput().put("VAL3", "v23");
				}
				if ("3".equals(testInput.getId())) {
					testOutput.getDataOutput().put("VAL1", "x");
					testOutput.getDataOutput().put("VAL2", "x");
					testOutput.getDataOutput().put("VAL3", "x");
				}

				// testReporter.reportTest(testOutput, batchId);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
