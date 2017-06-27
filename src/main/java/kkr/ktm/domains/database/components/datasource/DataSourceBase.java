package kkr.ktm.domains.database.components.datasource;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

public abstract class DataSourceBase implements DataSource {
	private static final java.util.logging.Logger LOGGING = java.util.logging.Logger.getLogger(DataSourceBase.class.getName());

	private int loginTimeout;
	private PrintWriter logWriter;

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return LOGGING;
	}

	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		this.logWriter = logWriter;
	}

	public void setLoginTimeout(int loginTimeout) throws SQLException {
		this.loginTimeout = loginTimeout;
	}
}
