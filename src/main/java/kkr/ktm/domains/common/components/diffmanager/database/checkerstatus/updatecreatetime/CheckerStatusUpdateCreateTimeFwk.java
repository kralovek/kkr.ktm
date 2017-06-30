package kkr.ktm.domains.common.components.diffmanager.database.checkerstatus.updatecreatetime;

import kkr.common.errors.ConfigurationException;


public abstract class CheckerStatusUpdateCreateTimeFwk {
	private boolean configured;

	protected String columnCreate;
	protected String columnUpdate;
	
	public void config() throws ConfigurationException {
		configured = false;
		if (columnUpdate == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter columnUpdate is not configured");
		}
		if (columnCreate == null) {
			throw new ConfigurationException(getClass().getSimpleName()
					+ ": Parameter columnCreate is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String getColumnCreate() {
		return columnCreate;
	}

	public void setColumnCreate(String columnCreate) {
		this.columnCreate = columnCreate;
	}

	public String getColumnUpdate() {
		return columnUpdate;
	}

	public void setColumnUpdate(String columnUpdate) {
		this.columnUpdate = columnUpdate;
	}
}
