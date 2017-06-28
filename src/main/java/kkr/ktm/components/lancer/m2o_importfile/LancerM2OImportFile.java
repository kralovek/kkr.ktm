package kkr.ktm.components.lancer.m2o_importfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import kkr.ktm.components.filemanager.FileManager;
import kkr.ktm.components.lancer.Lancer;
import kkr.ktm.components.runner.Runner;
import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffGroup;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsFile;
import kkr.ktm.utils.UtilsParameters;
import kkr.ktm.utils.collections.OrderFiFoMap;
import kkr.ktm.utils.errors.StopException;

public class LancerM2OImportFile extends LancerM2OImportFileFwk implements Lancer {
	private static final Logger LOG = Logger.getLogger(LancerM2OImportFile.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final String PARAM_EXCEPTION_CLASS = "EXCEPTION/CLASS";

	private static final String PARAM_EXCEPTION_MESSAGE = "EXCEPTION/MESSAGE";

	private static final String PARAM_EXCEPTION_DETAIL = "EXCEPTION/DETAIL";

	private static final String PARAM_TEST_ID = "TEST_ID";

	private static final String PARAM_DATA_INPUT = "DATA_INPUT";

	private static final String PARAM_DATA_OUTPUT = "DATA_OUTPUT";

	private static final String PARAM_TIME_BEGIN = "TIME";

	private static final String PARAM_TIME_END = "TIME_END";

	private static final String PARAM_TIME_LENGTH = "TIME_LENGTH";

	private static final String PARAM_OUTPUT_PARAMETERS = "PARAMETERS";

	private static final String PARAM_INPUT_DATA_FILE_NAME = "INPUT/DATA_FILE_NAME";

	private static final String PARAM_INPUT_DATA_FILE_GZ = "INPUT/DATA_FILE_GZ";

	private static final String PARAM_INPUT_DATA_NO_FILE = "INPUT/DATA_NO_FILE";

	private static final String PARAM_INPUT_DATA_NO_RUN = "INPUT/DATA_NO_RUN";

	private static final String PARAM_INPUT_DATA_FILE_ENCODING = "INPUT/DATA_FILE_ENCODING";

	public TestOutput lance(TestInput testInput, Map<String, Object> commonData) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			final Map<String, Object> resultMap = new OrderFiFoMap<String, Object>();
			try {
				final Map<String, Object> inputParameters = createInputParameters(testInput);

				//
				// DATA FILE
				//
				createDataFile(testInput, inputParameters, resultMap);

				//
				// Choose Diff Managers
				//
				List<DiffManager> diffManagers = chooseDiffManagers(testInput.getType());

				//
				// LOCK
				//
				lock();

				//
				// CURRENT
				//
				Map<String, List<DiffGroup>> diffGroupsCurrent = createCurrents(diffManagers);

				//
				// RUN
				//
				Date dateBegin = new Date();
				runTache(testInput, inputParameters, resultMap);
				Date dateEnd = new Date();

				//
				// STATISTICS
				//
				toParamStatistics(dateBegin, dateEnd, resultMap);

				//
				// DIFF
				//
				createDiff(diffManagers, diffGroupsCurrent, resultMap);

				unlock();

				//
				// Parameters to parametes
				//
				toParamParameters(resultMap);

			} catch (Exception ex) {
				resultMap.put(sysParamPrefix + PARAM_EXCEPTION_CLASS, ex.getClass().getSimpleName());
				resultMap.put(sysParamPrefix + PARAM_EXCEPTION_MESSAGE, ex.getMessage());
				resultMap.put(sysParamPrefix + PARAM_EXCEPTION_DETAIL, toStringException(ex));
				LOG.error(ex.getMessage());
			} finally {
				unlock();
			}

			TestOutputImpl testOutput = new TestOutputImpl(testInput.getSource(), testInput.getType(), testInput.getId());

			testOutput.getDataOutput().putAll(resultMap);

			LOG.trace("OK");

			return testOutput;
		} catch (StopException ex) {
			LOG.trace("OK");
			throw ex;
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, List<DiffGroup>> createCurrents(List<DiffManager> diffManagers) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<String, List<DiffGroup>> map = new HashMap<String, List<DiffGroup>>();
			for (DiffManager diffManager : diffManagers) {
				List<DiffGroup> diffGroups = diffManager.loadCurrents();
				map.put(diffManager.getName(), diffGroups);
			}
			LOG.trace("OK");
			return map;
		} finally {
			LOG.trace("END");
		}
	}

	private void createDiff(List<DiffManager> diffManagers, Map<String, List<DiffGroup>> diffGroupsCurrent, Map<String, Object> resultMap)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<String, List<DiffGroup>> diffGroupsDiff = new HashMap<String, List<DiffGroup>>();
			for (DiffManager diffManager : diffManagers) {
				List<DiffGroup> groupsCurrent = diffGroupsCurrent.get(diffManager.getName());
				List<DiffGroup> groupsDiff = diffManager.loadDiffs(groupsCurrent);
				diffGroupsDiff.put(diffManager.getName(), groupsDiff);
			}

			String resultXML = createResultDataXML(diffGroupsDiff);
			resultMap.put(sysParamPrefix + PARAM_DATA_OUTPUT, resultXML);

			generateTraceFileDiff(resultXML, new Date());

			final Map<String, Object> resultParserMapData = parametersParser.parse(resultXML);
			resultMap.putAll(resultParserMapData);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void toParamParameters(Map<String, Object> resultMap) {
		LOG.trace("BEGIN");
		try {
			Set<String> keys = new TreeSet<String>(resultMap.keySet());
			StringBuffer buffer = new StringBuffer();
			buffer.append(sysParamPrefix + PARAM_OUTPUT_PARAMETERS).append('\n');
			for (String key : keys) {
				buffer.append(key).append('\n');
			}
			resultMap.put(sysParamPrefix + PARAM_OUTPUT_PARAMETERS, buffer.toString());

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void toParamStatistics(Date dateBegin, Date dateEnd, Map<String, Object> resultMap) {
		LOG.trace("BEGIN");
		try {
			if (dateBegin != null) {
				resultMap.put(sysParamPrefix + PARAM_TIME_BEGIN, DATE_FORMAT.format(dateBegin));
				if (dateEnd != null) {
					// Time length in seconds
					final long timeLengthMS = dateEnd.getTime() - dateBegin.getTime();
					final double timeLengthS = ((double) timeLengthMS) / 1000.0;
					resultMap.put(sysParamPrefix + PARAM_TIME_LENGTH, String.valueOf(timeLengthS));
					resultMap.put(sysParamPrefix + PARAM_TIME_END, DATE_FORMAT.format(dateEnd));
				}
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void runTache(TestInput testInput, Map<String, Object> inputParameters, Map<String, Object> resultMap) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Boolean noRun = UtilsParameters.getStringBoolean(inputParameters, sysParamPrefix + PARAM_INPUT_DATA_NO_RUN, false);

			if (!noRun) {
				Runner runner = chooseRunner(testInput.getType());
				Map<String, Object> outputParameters = runner.run(inputParameters);
				resultMap.putAll(outputParameters);
			} else {
				LOG.info("Tache run was skiped for the test: [" + testInput.getId() + "]-{" + testInput.getType() + "}");
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void createDataFile(TestInput testInput, Map<String, Object> inputParameters, Map<String, Object> resultMap) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Boolean noFile = UtilsParameters.getStringBoolean(inputParameters, sysParamPrefix + PARAM_INPUT_DATA_NO_FILE, false);

			if (!noFile) {
				String templateData = templateArchiv.loadTemplate(testInput.getType() + ".data");

				String templateDataParsed = parametersFormatter.format(templateData, inputParameters);
				resultMap.put(sysParamPrefix + PARAM_DATA_INPUT, templateDataParsed);

				String filename = UtilsParameters.getStringParam(inputParameters, sysParamPrefix + PARAM_INPUT_DATA_FILE_NAME);
				if (filename == null) {
					if (destinationFilenamePattern != null) {
						filename = destinationFilenamePattern.format(new Date());
					} else {
						throw new ConfigurationException("The generated file is not specified by parameters nor configured");
					}
				}
				String fileEncoding = UtilsParameters.getStringParam(inputParameters, sysParamPrefix + PARAM_INPUT_DATA_FILE_ENCODING);
				Boolean gz = UtilsParameters.getStringBoolean(inputParameters, sysParamPrefix + PARAM_INPUT_DATA_FILE_GZ, false);

				FileManager fileManager = chooseFileManager(testInput.getType());
				String dirReception = chooseDirReception(testInput.getType());
				if (gz) {
					fileManager.contentToGzFile(templateDataParsed, filename, fileEncoding, dirReception);
				} else {
					fileManager.contentToFile(templateDataParsed, filename, fileEncoding, dirReception);
				}
			} else {
				LOG.info("No file will be generated for the test: [" + testInput.getId() + "]-{" + testInput.getType() + "}");
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, Object> createInputParameters(TestInput pTestUnit) {
		final Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put(PARAM_TEST_ID, pTestUnit.getId());
		inputParameters.putAll(pTestUnit.getDataInput());
		return inputParameters;
	}

	private String chooseDirReception(final String pSource) throws BaseException {
		String dirReception = UtilsParameters.chooseByTypePattern(this.dirDestination, dirDestinationByTypePattern, pSource);
		if (dirReception == null) {
			throw new ConfigurationException("No directory Reception is defined for the source: " + pSource);
		}
		return dirReception;
	}

	private FileManager chooseFileManager(final String pSource) throws BaseException {
		FileManager fileManager = UtilsParameters.chooseByTypePattern(this.fileManager, fileManagerByTypePattern, pSource);
		if (fileManager == null) {
			throw new ConfigurationException("No FileManager is defined for the source: " + pSource);
		}
		return fileManager;
	}

	private List<DiffManager> chooseDiffManagers(final String pSource) throws BaseException {
		List<DiffManager> diffManagers = UtilsParameters.chooseByTypePattern(this.diffManagers, diffManagersByTypePattern, pSource);
		// No test is necessary
		return diffManagers;
	}

	private Runner chooseRunner(final String pSource) throws BaseException {
		Runner runner = UtilsParameters.chooseByTypePattern(this.runner, runnerByTypePattern, pSource);
		if (runner == null) {
			throw new ConfigurationException("No Runner is defined for the source: " + pSource);
		}
		return runner;
	}

	private String createResultDataXML(Map<String, List<DiffGroup>> namesGroups) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<DIFFS>").append('\n');
		for (Map.Entry<String, List<DiffGroup>> entry : namesGroups.entrySet()) {
			String diffTagName = adaptXmlTag(entry.getKey());
			List<DiffGroup> diffGroups = entry.getValue();
			buffer.append("    ").append("<" + diffTagName + ">").append('\n');
			for (DiffGroup diffGroup : diffGroups) {
				String groupTagName = adaptXmlTag(diffGroup.getName());
				buffer.append("        ").append("<" + groupTagName + ">").append('\n');
				if (diffGroup.getItems() != null) {
					for (DiffItem diffItem : diffGroup.getItems()) {
						buffer.append("            ").append("<ITEM ").append("name=\"" + diffItem.getName() + "\" ")
								.append("status=\"" + diffItem.getStatus() + "\" ").append("index=\"" + diffItem.getIndex().toString() + "\" ")
								.append(">").append("\n");
						if (diffItem.getParameters() != null) {
							for (Map.Entry<String, String> entryParameter : diffItem.getParameters().entrySet()) {
								String value = adaptXmlValue(entryParameter.getValue());
								buffer.append("                ").append("<" + entryParameter.getKey() + ">")
										//
										// TODO: CDATA
										//
										.append(value).append("</" + entryParameter.getKey() + ">").append("\n");
							}
						}
						buffer.append("            ").append("</ITEM>").append("\n");
					}
				}
				buffer.append("        ").append("</" + groupTagName + ">").append('\n');
			}
			buffer.append("    ").append("</" + diffTagName + ">").append('\n');
		}
		buffer.append("</DIFFS>").append('\n');

		return buffer.toString();
	}

	private static String adaptXmlValue(String value) {
		if (value == null) {
			return value;
		}
		String valueMod = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		return valueMod;
	}

	private static String adaptXmlTag(String tag) {
		if (tag == null) {
			return null;
		}
		String tagAdapted = tag.replace(":", "_").replace("/", "_").replace("\\", "_");
		return tagAdapted;
	}

	private void generateTraceFileDiff(String diff, Date date) throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (traceDiffPattern != null) {
				String path = traceDiffPattern.format(date);
				File file = new File(path);
				LOG.debug("Logging the DIFF to: " + file.toString());
				if (file.getParentFile() != null && !file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
					throw new TechnicalException("Cannot create the directory: " + file.getParentFile().getAbsolutePath());
				}
				UtilsFile.getInstance().contentToFile(diff, file);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private String toStringException(final Exception pException) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(byteArrayOutputStream);
		pException.printStackTrace(printStream);
		printStream.close();
		return byteArrayOutputStream.toString();
	}

	private void lock() throws BaseException {
		if (locker != null) {
			locker.lock();
		}
	}

	private void unlock() throws BaseException {
		if (locker != null) {
			locker.unlock();
		}
	}
}
