package kkr.ktm.components.tablereader.sql;

import kkr.ktm.domains.database.components.datasource.DataSource;
import kkr.ktm.exception.ConfigurationException;

public abstract class TableReaderSqlFwk {
	private boolean configured;

	protected DataSource dataSource;

	public void config() throws ConfigurationException {
		configured = false;
		if (dataSource == null) {
			throw new ConfigurationException("Parameter 'dataSource' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
