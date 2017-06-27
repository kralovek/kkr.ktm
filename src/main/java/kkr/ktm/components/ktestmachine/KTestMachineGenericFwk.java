package kkr.ktm.components.ktestmachine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import kkr.ktm.components.batchreporter.BatchReporter;
import kkr.ktm.components.lancer.Lancer;
import kkr.ktm.components.testsinitializer.TestsInitializer;
import kkr.ktm.domains.tests.components.testloader.TestLoader;
import kkr.ktm.domains.tests.components.testreporter.TestReporter;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.utils.UtilsParameters;

/**
 * KTestMachineGenericFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class KTestMachineGenericFwk {
	private boolean configured;

	protected String sysParamPrefix;

	protected File stopFile = new File("STOP");

	protected TestLoader testLoader;

	protected TestReporter testReporter;

	protected Lancer lancer;
	private Map<String, Lancer> lancerByType;
	protected Map<Pattern, Lancer> lancerByTypePattern;

	protected List<BatchReporter> batchReporters;

	private String help;

	protected TestsInitializer testsInitializer;

	public void config() throws BaseException {
		configured = false;
		if (testLoader == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": The parameter testLoader is not configured");
		}
		if (testReporter == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": The parameter testReporter is not configured");
		}
		if (lancerByType == null) {
			lancerByType = new HashMap<String, Lancer>();
		}
		if (lancer == null && lancerByType.isEmpty()) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Neither the parameter testLancer nor lancerByType is configured");
		}
		lancerByTypePattern = UtilsParameters.toByTypePattern(getClass(), lancerByType);

		if (batchReporters == null) {
			batchReporters = new ArrayList<BatchReporter>();
		}

		if (sysParamPrefix == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter sysPrefix is not configured");
		} else if (!sysParamPrefix.isEmpty() && !sysParamPrefix.endsWith("/")) {
			sysParamPrefix += "/";
		}
		if (testsInitializer == null) {
			// OK
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

	public Lancer getLancer() {
		return lancer;
	}

	public void setLancer(Lancer lancer) {
		this.lancer = lancer;
	}

	public Map<String, Lancer> getLancerByType() {
		return lancerByType;
	}

	public void setLancerByType(Map<String, Lancer> lancerByType) {
		this.lancerByType = lancerByType;
	}

	public File getStopFile() {
		return stopFile;
	}

	public void setStopFile(File stopFile) {
		this.stopFile = stopFile;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}

	public List<BatchReporter> getBatchReporters() {
		return batchReporters;
	}

	public void setBatchReporters(List<BatchReporter> batchReporters) {
		this.batchReporters = batchReporters;
	}

	public TestsInitializer getTestsInitializer() {
		return testsInitializer;
	}

	public void setTestsInitializer(TestsInitializer testsInitializer) {
		this.testsInitializer = testsInitializer;
	}
}
