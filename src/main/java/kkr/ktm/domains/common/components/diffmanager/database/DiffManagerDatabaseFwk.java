package kkr.ktm.domains.common.components.diffmanager.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import kkr.ktm.domains.database.components.datasource.DataSource;
import kkr.ktm.exception.ConfigurationException;

public abstract class DiffManagerDatabaseFwk {
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private boolean configured;

	protected String name;
	protected List<TableInfo> tableInfos;
	protected DataSource dataSource;

	private String dateFormat;
	protected DateFormat patternDate;

	public void config() throws ConfigurationException {
		configured = false;
		if (tableInfos == null) {
			tableInfos = new ArrayList<TableInfo>();
		} else {
			for (TableInfo tableInfo : tableInfos) {
				if (tableInfo.getColumnName() == null) {
					throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'TableInfo.columnName' is not configured");
				}
				if (tableInfo.getColumnSort() == null) {
					throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'TableInfo.columnSort' is not configured");
				}
			}
		}
		if (dateFormat != null) {
			patternDate = new SimpleDateFormat(dateFormat);
		} else {
			patternDate = new SimpleDateFormat(DATE_FORMAT);
		}
		if (name == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter name is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public List<TableInfo> getTableInfos() {
		return tableInfos;
	}

	public void setTableInfos(List<TableInfo> tableInfos) {
		this.tableInfos = tableInfos;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
