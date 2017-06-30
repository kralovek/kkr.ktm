package kkr.ktm.domains.orchestrator.components.batchktm;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.log4j.Logger;

import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.domains.tests.data.TestOutput;
import kkr.common.errors.BaseException;
import kkr.common.utils.UtilsString;
import kkr.ktm.utils.errors.StopException;

public class BatchKtm extends BatchKtmFwk {
	private static final Logger LOG = Logger.getLogger(BatchKtm.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final String PARAM_TIME_BEGIN = "TIME.BEGIN";
	private static final String PARAM_TIME_END = "TIME.END";
	private static final String PARAM_TIME_DELTA = "TIME.DELTA";

	public void run(String batchId, String source) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String prefix = UtilsString.isEmpty(sysPrefix) ? "" : sysPrefix + ".";

			Collection<TestInput> testsInput = testLoader.loadTests(source);

			int iTest = 0;
			int testsCount = testsInput.size();

			int resultOk = 0;
			int resultKo = 0;
			int resultSkip = 0;

			Collection<Integer> groupsKo = new HashSet<Integer>();

			for (TestInput testInput : testsInput) {
				iTest++;

				if (!trafficLights.isRun()) {
					LOG.info("");
					LOG.info("=============================");
					LOG.info("EXECUTION STOPPED BY THE USER");
					LOG.info("=============================");
				}

				LOG.info("..................................................................");

				Date dateBegin = new Date();
				LOG.info(DATE_FORMAT.format(dateBegin) + " Test: (" + iTest + "/" + testsCount + ")" + testInput.toString());

				if (groupsKo.contains(testInput.getGroup())) {
					LOG.info(DATE_FORMAT.format(dateBegin) + " - SKIP -");
					testReporter.skipTest(testInput, batchId);
					continue;
				}

				try {
					TestOutput testOutput = testLancer.lanceTest(testInput);
					Date dateEnd = new Date();
					String dateDetla = UtilsString.toStringDateDelta(dateBegin, dateEnd);

					testOutput.getDataOutput().put(prefix + PARAM_TIME_BEGIN, dateBegin);
					testOutput.getDataOutput().put(prefix + PARAM_TIME_END, dateEnd);
					testOutput.getDataOutput().put(prefix + PARAM_TIME_DELTA, dateDetla);

					Status status = testReporter.reportTest(testOutput, batchId);

					switch (status) {
						case OK :
							resultOk++;
							LOG.info(DATE_FORMAT.format(dateBegin) + " *  OK  *");
							break;

						case KO :
							resultKo++;
							LOG.info(DATE_FORMAT.format(dateBegin) + "### KO ###");
							groupsKo.add(testInput.getGroup());
							break;

						case SKIP :
							resultSkip++;
							LOG.info(DATE_FORMAT.format(dateBegin) + " - SKIP - ");
							groupsKo.add(testInput.getGroup());
							break;

						default :
							throw new IllegalStateException("Unsupported DiffStatus: " + status);
					}

					LOG.info(dateDetla);
				} catch (StopException ex) {
					LOG.info(DATE_FORMAT.format(dateBegin) + " * STOP *");

					LOG.info("");
					LOG.info("=============================");
					LOG.info("EXECUTION STOPPED BY THE USER");
					LOG.info("=============================");

					break;
				}
			}

			LOG.info("");
			LOG.info("=============================");
			LOG.info("RESULTS: " + (resultOk + resultKo));
			if (resultOk > 0) {
				LOG.info(" *  OK: " + resultOk);
			}
			if (resultKo > 0) {
				LOG.info(" #  KO: " + resultKo);
			}
			if (resultSkip > 0) {
				LOG.info(" -  SKIP: " + resultSkip);
			}
			LOG.info("=============================");

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
