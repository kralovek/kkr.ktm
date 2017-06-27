package kkr.ktm.components.locker.database;

import kkr.ktm.domains.database.components.datasource.DataSource;
import kkr.ktm.exception.ConfigurationException;

public abstract class LockerDatabaseFwk {

	private boolean configured;

	protected String whoami;
	protected DataSource dataSource;
	protected Long waitMax;
	protected Long waitInterval;

	public void config() throws ConfigurationException {
		configured = false;
		if (whoami == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter whoami is not configured");
		} else if (whoami.length() > 10) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter whoami can have max 10 characters");
		}
		if (dataSource == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter dataSource is not configured");
		}
		if (waitMax == null) {
			// OK
		}
		if (waitInterval == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter waitInterval is not configured");
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

	public String getWhoami() {
		return whoami;
	}

	public void setWhoami(String whoami) {
		this.whoami = whoami;
	}

	public Long getWaitMax() {
		return waitMax;
	}

	public void setWaitMax(Long waitMax) {
		this.waitMax = waitMax;
	}

	public Long getWaitInterval() {
		return waitInterval;
	}

	public void setWaitInterval(Long waitInterval) {
		this.waitInterval = waitInterval;
	}
}
