package kkr.ktm.components.lancer.template;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import kkr.ktm.components.executant.Executant;
import kkr.ktm.components.lancer.Lancer;
import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.utils.UtilsFile;

/**
 * LancerTemplateBased
 * 
 * @author KRALOVEC-99999
 */
public class LancerTemplateBased extends LancerTemplateBasedFwk implements Lancer {
	private static final Logger LOG = Logger.getLogger(LancerTemplateBased.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");

	/**
	 * Parameter used for result content
	 */
	private static final String PARAM_TEST_ID = "SYSTEM/TEST_ID";

	/**
	 * Parameter used for response content
	 */
	private static final String PARAM_RESPONSE = "SYSTEM/RESPONSE";

	/**
	 * Parameter used for request content
	 */
	private static final String PARAM_REQUEST = "SYSTEM/REQUEST";

	/**
	 * Parameter used for time
	 */
	private static final String PARAM_TIME = "SYSTEM/TIME";

	/**
	 * Parameter used for time
	 */
	private static final String PARAM_TIME_LENGTH = "SYSTEM/TIME_LENGTH";

	private static final String PARAM_EXCEPTION_CLASS = "EXCEPTION/CLASS";

	private static final String PARAM_EXCEPTION_MESSAGE = "EXCEPTION/MESSAGE";

	private static final String PARAM_EXCEPTION_DETAIL = "EXCEPTION/DETAIL";

	public TestOutput lance(TestInput testInput, Map<String, Object> commonData) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			final Map<String, Object> resultMap = new TreeMap<String, Object>();

			final String nameRoot = nameRoot(testInput);

			validateTestUnit(testInput);

			final String template = templateArchiv.loadTemplate(testInput.getType());

			final Map<String, Object> inputParameters = new HashMap<String, Object>();
			inputParameters.putAll(systemInputParameters(testInput));
			inputParameters.putAll(testInput.getDataInput());

			final String templateParsed = templateParser.parse(template, inputParameters);
			resultMap.put(PARAM_REQUEST, templateParsed);

			if (traceSource) {
				final File fileTemplateParsed = new File(dirTrace, nameRoot + "_" + "REQUEST.txt");
				UtilsFile.getInstance().contentToFile(templateParsed, fileTemplateParsed);
			}

			final Executant executant = chooseExecutant(testInput.getType());
			if (executant == null) {
				throw new ConfigurationException("No executant configured for the test type: " + testInput.getType());
			}

			String result = null;
			BaseException baseException = null;
			Date dateBegin = null;
			Date dateEnd = null;
			try {
				boolean body = false;
				for (int i = 0; i < repeatUnsuccessful && !body; i++) {
					//
					// Withouot this the WS call is KO
					//
					if (i != 0) {
						pause(100);
					}

					dateBegin = new Date();
					result = executant.execute(templateParsed);
					resultMap.put(PARAM_RESPONSE, result);

					dateEnd = new Date();
					body = containsBody(result);
					if (!body) {
						dateBegin = null;
						dateEnd = null;
					}
					if (traceResult) {
						final File fileResult = new File(dirTrace, nameRoot + "_" + "RESPONSE" + (body ? "" : "_KO[" + i + "]") + ".txt");
						UtilsFile.getInstance().contentToFile(result, fileResult);
					}
				}
			} catch (final BaseException ex) {
				baseException = ex;
			}

			final Map<String, Object> resultParserMap = resultParser.parse(result);

			if (baseException != null) {
				resultMap.put(PARAM_EXCEPTION_CLASS, baseException.getClass().getSimpleName());
				resultMap.put(PARAM_EXCEPTION_MESSAGE, baseException.getMessage());
				resultMap.put(PARAM_EXCEPTION_DETAIL, baseException.toString());
			}

			resultMap.putAll(resultParserMap);

			if (dateBegin != null) {
				resultMap.put(PARAM_TIME, DATE_FORMAT.format(dateBegin));
				if (dateEnd != null) {
					// Time length in seconds
					final long timeLengthMS = dateEnd.getTime() - dateBegin.getTime();
					final double timeLengthS = ((double) timeLengthMS) / 1000.0;
					resultMap.put(PARAM_TIME_LENGTH, String.valueOf(timeLengthS));
				}
			}

			validateResult(resultMap);

			TestOutputImpl testOutput = new TestOutputImpl(testInput.getSource(), testInput.getType(), testInput.getId());

			testOutput.getDataOutput().putAll(resultMap);

			LOG.trace("OK");
			return testOutput;
		} finally {
			LOG.trace("END");
		}
	}

	private boolean containsBody(String pContent) {
		return pContent.contains("<?xml") || pContent.contains("xmlns");
	}

	private void validateResult(final Map<String, Object> pResultMap) throws BaseException {
	}

	private void validateTestUnit(TestInput testUnit) throws BaseException {
	}

	private String nameRoot(TestInput pTestUnit) {
		return DATE_FORMAT.format(new Date()) + "_" + pTestUnit.getType() + "_" + pTestUnit.getId();
	}

	private Executant chooseExecutant(final String pSource) {
		if (executantByType != null && executantByType.containsKey(pSource)) {
			return executantByType.get(pSource);
		}
		return executant;
	}

	private void pause(final int pMilis) {
		try {
			Thread.sleep(pMilis);
		} catch (final InterruptedException ex) {
			//
		}
	}

	private Map<String, Object> systemInputParameters(TestInput pTestUnit) {
		final Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put(PARAM_TEST_ID, pTestUnit.getId());
		return inputParameters;
	}
}
