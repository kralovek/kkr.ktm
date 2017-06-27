package kkr.ktm.components.lancer.infotel_executesql;

import kkr.ktm.components.templatearchiv.TemplateArchiv;
import kkr.ktm.components.templateparser.TemplateParser;
import kkr.ktm.domains.database.components.datasource.DataSource;
import kkr.ktm.exception.ConfigurationException;

public abstract class LancerInfotelExecuteSqlFwk {
	private boolean configured;

	protected TemplateArchiv templateArchiv;
	protected TemplateParser templateParser;
	protected DataSource dataSource;
	protected String sysParamPrefix;

	public void config() throws ConfigurationException {
		configured = false;
		if (templateArchiv == null) {
			throw new ConfigurationException("Parameter 'templateArchiv' is not configured");
		}
		if (templateParser == null) {
			throw new ConfigurationException("Parameter 'templateParser' is not configured");
		}
		if (dataSource == null) {
			throw new ConfigurationException("Parameter 'dataSource' is not configured");
		}
		if (sysParamPrefix == null || sysParamPrefix.isEmpty()) {
			sysParamPrefix = "";
		} else {
			sysParamPrefix += "/";
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public TemplateArchiv getTemplateArchiv() {
		return templateArchiv;
	}

	public void setTemplateArchiv(TemplateArchiv templateArchiv) {
		this.templateArchiv = templateArchiv;
	}

	public TemplateParser getTemplateParser() {
		return templateParser;
	}

	public void setTemplateParser(TemplateParser templateParser) {
		this.templateParser = templateParser;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}
}
