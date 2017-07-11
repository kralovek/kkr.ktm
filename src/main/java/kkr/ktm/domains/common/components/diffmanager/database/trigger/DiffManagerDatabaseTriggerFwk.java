package kkr.ktm.domains.common.components.diffmanager.database.trigger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import kkr.common.components.datasource.DataSource;
import kkr.common.errors.ConfigurationException;
import kkr.ktm.utils.UtilsKtm;

public abstract class DiffManagerDatabaseTriggerFwk {
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	private boolean configured;
	protected String code;
	protected List<TableInfo> tableInfos;
	protected DataSource dataSource;

	private String dateFormat;
	protected DateFormat patternDate;

	public void config() throws ConfigurationException {
		configured = false;
		if (tableInfos == null) {
			tableInfos = new ArrayList<TableInfo>();
		}
		if (dateFormat != null) {
			patternDate = new SimpleDateFormat(dateFormat);
		} else {
			patternDate = new SimpleDateFormat(DATE_FORMAT);
		}
		code = UtilsKtm.checkEntityName(code, "code");
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
}
