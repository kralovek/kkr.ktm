package kkr.ktm.components.ktestmachine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import kkr.ktm.components.batchreporter.BatchReporter;
import kkr.ktm.components.batchreporter.TestReport;
import kkr.ktm.components.lancer.Lancer;
import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.utils.UtilsParameters;
import kkr.ktm.utils.errors.StopException;
import kkr.ktm.utils.errors.TreatErrors;

public class KTestMachineGeneric extends KTestMachineGenericFwk implements KTestMachine {
	private static final Logger LOG = Logger.getLogger(KTestMachineGeneric.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final String PARAM_INPUT_DATA_STOP = "INPUT/DATA_STOP";

	private static final String PARAM_TIME_END = "TIME/END";

	private static final String PARAM_TIME_LENGTH = "TIME/LENGTH";

	private static final String PARAM_TIME_BEGIN = "TIME/BEGIN";

	private static final String PARAM_OUTPUTS = "DATA/OUTPUT";

	public boolean test(String batchId, String... sources) {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			Map<String, Object> commonData = new LinkedHashMap<String, Object>();

			List<TestReport> testReports = new ArrayList<TestReport>();

			boolean resultAll = true;
			List<TestInput> testUnits = new ArrayList<TestInput>();
			for (String source : sources) {
				Collection<TestInput> testUnitsSource = null; //testLoader.loadTests(source);
				testUnits.addAll(testUnitsSource);
			}
			if (testUnits != null) {
				if (testsInitializer != null) {
					testsInitializer.initialize(testUnits, commonData);
				}

				LOG.trace("BEGIN");
				try {
					int testsCount = testUnits.size();
					int iTest = 0;
					boolean stoped = false;
					// To liberate the memory
					Iterator<TestInput> iterator = testUnits.iterator();
					while (iterator.hasNext()) {
						final TestInput testInput = iterator.next();
						iterator.remove();
						if (!stoped && stopFile != null && stopFile.exists()) {
							stoped = true;
							if (!stopFile.delete()) {
								stopFile.deleteOnExit();
							}
							LOG.info("=========================================");
							LOG.info("EXECUTION OF THE TESTS STOPED BY THE USER");
							LOG.info("=========================================");
						}

						Boolean resultTest = null;
						iTest++;

						Date timeBegin = null;
						Date timeEnd = null;

						if (!stoped) {
							stoped = checkStop(testInput);
							LOG.trace("BEGIN");
							try {
								LOG.info("..............................");
								LOG.info("Test:" + " (" + iTest + "/" + testsCount + ")" + " [ID: " + testInput.getId() + "]" + " [TYPE: "
										+ testInput.getType() + "]" + " [SOURCE: " + testInput.getSource() + "]");

								TestOutput testOutput = null;
								Lancer lancer = chooseLancer(testInput.getType());
								try {
									timeBegin = new Date();
									testOutput = lancer.lance(testInput, commonData);
									timeEnd = new Date();

									toParamOutputs(testOutput.getDataOutput());
									testOutput.getDataOutput().put(sysParamPrefix + PARAM_TIME_BEGIN, DATE_FORMAT.format(timeBegin));
									testOutput.getDataOutput().put(sysParamPrefix + PARAM_TIME_END, DATE_FORMAT.format(timeEnd));
									testOutput.getDataOutput().put(sysParamPrefix + PARAM_TIME_LENGTH, timeEnd.getTime() - timeBegin.getTime());
								} catch (StopException ex) {
									stoped = true;
								}
								resultTest = null; // testReporter.reportTest(testOutput, // TODO: check testOutput =
								// null
								// batchId);
								resultAll = resultAll && resultTest;
								if (resultTest) {
									LOG.info(" * OK *");
								} else {
									LOG.info("## KO ##");
								}
								LOG.trace("OK");
							} finally {
								LOG.trace("END");
							}
						}

						TestReportImpl testReportImpl = new TestReportImpl();
						testReportImpl.setSource(testInput.getSource());
						testReportImpl.setType(testInput.getType());
						testReportImpl.setId(testInput.getId());
						testReportImpl.setOk(resultTest);
						testReportImpl.setName(testInput.getName());
						testReportImpl.setDescription(testInput.getDescription());
						testReportImpl.setTimeBegin(timeBegin);
						testReportImpl.setTimeEnd(timeEnd);

						testReports.add(testReportImpl);
					}
					LOG.trace("OK");
				} finally {
					LOG.trace("END");
				}
			}
			printReports(testReports, batchId);

			LOG.trace("OK");
			return resultAll;
		} catch (final Exception ex) {
			TreatErrors.treatException(ex);
			LOG.trace("OK");
			return false;
		} finally {
			LOG.trace("END");
		}
	}

	private void printReports(List<TestReport> testReports, final String batchId) throws BaseException {
		LOG.trace("BEGIN");
		try {
			for (BatchReporter batchReporter : batchReporters) {
				LOG.info("Reporting batch results to: " + batchReporter.getClass().getSimpleName());
				batchReporter.report(batchId, testReports);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private Lancer chooseLancer(String key) throws BaseException {
		Lancer lancer = UtilsParameters.chooseByTypePattern(this.lancer, lancerByTypePattern, key);
		if (lancer == null) {
			throw new ConfigurationException("No TestLancer is specified for: " + key);
		}
		return lancer;
	}

	private boolean checkStop(TestInput pTestUnit) throws BaseException {
		return UtilsParameters.getStringBoolean(pTestUnit.getDataInput(), sysParamPrefix + PARAM_INPUT_DATA_STOP, false);
	}

	private void toParamOutputs(Map<String, Object> resultMap) {
		LOG.trace("BEGIN");
		try {
			Set<String> keys = new TreeSet<String>(resultMap.keySet());
			StringBuffer buffer = new StringBuffer();
			buffer.append(sysParamPrefix + PARAM_OUTPUTS).append('\n');
			for (String key : keys) {
				buffer.append(key).append('\n');
			}
			resultMap.put(sysParamPrefix + PARAM_OUTPUTS, buffer.toString());

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
