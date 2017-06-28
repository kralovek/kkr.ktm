package kkr.ktm.components.runner.shellcommand;

import kkr.ktm.domains.common.components.parametersformater.ParametersFormatter;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;

public abstract class RunnerShellCommandFwk {
	private boolean configured;
	protected String command;

	protected ParametersFormatter parametersFormatter;

	protected String sysParamPrefix;

	public void config() throws BaseException {
		configured = false;
		if (parametersFormatter == null) {
			// OK
		}
		if (command == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter command is not configured");
		}
		if (sysParamPrefix == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter sysPrefix is not configured");
		} else if (!sysParamPrefix.isEmpty() && !sysParamPrefix.endsWith("/")) {
			sysParamPrefix += "/";
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ParametersFormatter getTemplateParser() {
		return parametersFormatter;
	}

	public void setTemplateParser(ParametersFormatter parametersFormatter) {
		this.parametersFormatter = parametersFormatter;
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}
}
