package kkr.ktm.components.testsinitializer.infotel_table;

import kkr.ktm.components.tablereader.TableReader;
import kkr.ktm.components.templatearchiv.TemplateArchiv;
import kkr.ktm.components.templateparser.TemplateParser;
import kkr.ktm.exception.ConfigurationException;

public abstract class TestsInitializerInfotelTableFwk {
	private boolean configured;

	protected String keyData;
	protected TemplateArchiv templateArchiv;
	protected TemplateParser templateParser;
	protected TableReader tableReader;

	public void config() throws ConfigurationException {
		configured = false;
		if (templateArchiv == null) {
			throw new ConfigurationException("Parameter 'templateArchiv' is not configured");
		}
		if (templateParser == null) {
			throw new ConfigurationException("Parameter 'templateParser' is not configured");
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

	public TemplateParser getTemplateParser() {
		return templateParser;
	}

	public void setTemplateParser(TemplateParser templateParser) {
		this.templateParser = templateParser;
	}

	public TableReader getTableReader() {
		return tableReader;
	}

	public void setTableReader(TableReader tableReader) {
		this.tableReader = tableReader;
	}
}
