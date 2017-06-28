package kkr.ktm.domains.common.components.diffmanager.databasetrigger;

import java.util.List;

import kkr.ktm.exception.ConfigurationException;

public class TableInfoFwk {

	private boolean configured;

	protected String ktmSchema;
	protected String ktmName;
	private String ktmNamePrefix;

	protected String schema;
	protected String name;
	protected List<String> columnsPK;

	protected Boolean joinStatus;
	
	public void config() throws ConfigurationException {
		configured = false;
		if (schema == null) {
			// OK
		}
		if (name == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter name is not configured");
		}
		if (ktmSchema == null) {
			ktmSchema = schema;
		}
		if (ktmNamePrefix != null) {
			ktmName = ktmNamePrefix + name;
		} else {
			ktmName = name;
		}
		if (name == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter name is not configured");
		}
		if (columnsPK == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter columnsPK is not configured");
		} else if (columnsPK.size() == 0) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter-list columnsPK may not be empty");
		}
		if (joinStatus == null) {
			joinStatus = false;
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getColumnsPK() {
		return columnsPK;
	}

	public void setColumnsPK(List<String> columnsPK) {
		this.columnsPK = columnsPK;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getKtmSchema() {
		return ktmSchema;
	}

	public void setKtmSchema(String ktmSchema) {
		this.ktmSchema = ktmSchema;
	}

	public String getKtmNamePrefix() {
		return ktmNamePrefix;
	}

	public void setKtmNamePrefix(String ktmNamePrefix) {
		this.ktmNamePrefix = ktmNamePrefix;
	}

	public boolean getJoinStatus() {
		return joinStatus;
	}

	public void setJoinStatus(boolean joinStatus) {
		this.joinStatus = joinStatus;
	}
}
