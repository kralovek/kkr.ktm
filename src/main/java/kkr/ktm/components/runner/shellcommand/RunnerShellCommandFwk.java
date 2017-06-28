package kkr.ktm.components.runner.shellcommand;

import kkr.ktm.domains.common.components.formaterparameters.FormatterParameters;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;

public abstract class RunnerShellCommandFwk {
	private boolean configured;
	protected String command;

	protected FormatterParameters formatterParameters;

	protected String sysParamPrefix;

	public void config() throws BaseException {
		configured = false;
		if (formatterParameters == null) {
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

	public FormatterParameters getTemplateParser() {
		return formatterParameters;
	}

	public void setTemplateParser(FormatterParameters formatterParameters) {
		this.formatterParameters = formatterParameters;
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}
}
