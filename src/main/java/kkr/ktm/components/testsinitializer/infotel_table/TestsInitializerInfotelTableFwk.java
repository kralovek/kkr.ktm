package kkr.ktm.components.testsinitializer.infotel_table;

import kkr.ktm.components.tablereader.TableReader;
import kkr.ktm.components.templatearchiv.TemplateArchiv;
import kkr.ktm.domains.common.components.parametersformater.ParametersFormatter;
import kkr.ktm.exception.ConfigurationException;

public abstract class TestsInitializerInfotelTableFwk {
	private boolean configured;

	protected String keyData;
	protected TemplateArchiv templateArchiv;
	protected ParametersFormatter parametersFormatter;
	protected TableReader tableReader;

	public void config() throws ConfigurationException {
		configured = false;
		if (templateArchiv == null) {
			throw new ConfigurationException("Parameter 'templateArchiv' is not configured");
		}
		if (parametersFormatter == null) {
			throw new ConfigurationException("Parameter 'parametersFormatter' is not configured");
		}
		if (tableReader == null) {
			throw new ConfigurationException("Parameter 'tableReader' is not configured");
		}
		if (keyData == null) {
			throw new ConfigurationException("Parameter 'keyData' is not configured");
		}
		configured = true;
	}

	public String getKeyData() {
		return keyData;
	}

	public void setKeyData(String keyData) {
		this.keyData = keyData;
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

	public ParametersFormatter getTemplateParser() {
		return parametersFormatter;
	}

	public void setTemplateParser(ParametersFormatter parametersFormatter) {
		this.parametersFormatter = parametersFormatter;
	}

	public TableReader getTableReader() {
		return tableReader;
	}

	public void setTableReader(TableReader tableReader) {
		this.tableReader = tableReader;
	}
}
