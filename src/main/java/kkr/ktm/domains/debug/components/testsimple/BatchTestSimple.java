package kkr.ktm.domains.debug.components.testsimple;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.ktm.exception.BaseException;

public class BatchTestSimple extends BatchTestSimpleFwk {
	private static final Logger LOG = Logger.getLogger(BatchTestSimple.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");

	public void run() throws BaseException {
		LOG.trace("BEGIN");
		try {
			String batchId = DATE_FORMAT.format(new Date());
			String source = "KTM.xlsx";

			Collection<TestInput> testsInput = testLoader.loadTests(source);

			for (TestInput testInput : testsInput) {
				TestOutput testOutput = new TestOutputImpl(testInput.getName(), testInput.getDescription(), testInput.getSource(),
						testInput.getType(), testInput.getCode(), null);

				if ("1".equals(testInput.getCode())) {
					testOutput.getDataOutput().put("VAL1", "v11");
					testOutput.getDataOutput().put("VAL2", "v12");
					testOutput.getDataOutput().put("VAL3", "v13");
				}
				if ("2".equals(testInput.getCode())) {
					testOutput.getDataOutput().put("VAL1", "v21");
					testOutput.getDataOutput().put("VAL2", "x");
					testOutput.getDataOutput().put("VAL3", "v23");
				}
				if ("3".equals(testInput.getCode())) {
					testOutput.getDataOutput().put("VAL1", "x");
					testOutput.getDataOutput().put("VAL2", "x");
					testOutput.getDataOutput().put("VAL3", "x");
				}

				testReporter.reportTest(testOutput, batchId);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
