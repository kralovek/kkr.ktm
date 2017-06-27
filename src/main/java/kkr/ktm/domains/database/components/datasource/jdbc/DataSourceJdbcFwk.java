package kkr.ktm.domains.database.components.datasource.jdbc;

import kkr.ktm.domains.database.components.datasource.DataSourceBase;
import kkr.ktm.exception.ConfigurationException;

public abstract class DataSourceJdbcFwk extends DataSourceBase {
	private boolean configured;

	protected String name;
	protected String driver;
	protected String schema;
	protected String password;
	protected String host;
	protected Integer port;
	protected String instance;
	protected String service;

	public void config() throws ConfigurationException {
		configured = false;
		if (driver == null) {
			throw new ConfigurationException("Parameter 'driver' is not configured");
		}
		if (schema == null) {
			throw new ConfigurationException("Parameter 'schema' is not configured");
		}
		if (password == null) {
			throw new ConfigurationException("Parameter 'password' is not configured");
		}
		if (port == null) {
			throw new ConfigurationException("Parameter 'port' is not configured");
		}
		if (instance == null) {
			throw new ConfigurationException("Parameter 'instance' is not configured");
		}
		if (service == null || service.isEmpty()) {
			service = null;
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
