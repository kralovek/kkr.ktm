package kkr.ktm.domains.database.components.datasource.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kkr.ktm.domains.database.components.datasource.DataSource;

public class DataSourceJdbc extends DataSourceJdbcFwk implements DataSource {
	private static Log LOG = LogFactory.getLog(DataSourceJdbc.class);

	public Connection getConnection() throws SQLException {
		Connection connection = getConnection(schema, password);
		return connection;
	}

	public Connection getConnection(String schema, String password) throws SQLException {
		LOG.trace("BEGIN: [" + name + "] " + schema);
		try {
			testConfigured();
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException ex) {
				throw new SQLException("Impossible de load db driver class: " + driver);
			}

			String url;
			if (service != null) {
				url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCPS)(HOST=" + host + ")(PORT=" + port + "))(CONNECT_DATA=(SERVICE_NAME="
						+ service + ")))";
			} else {
				url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + instance;
			}

			LOG.info(" [" + name + "] " + schema + " URL: " + url);

			Connection connection = null;
			try {
				connection = DriverManager.getConnection(url, schema, password);
				LOG.trace("OK");
				return connection;
			} catch (SQLException ex) {
				throw new SQLException("Impossible to connect to database: " + url + ", " + schema, ex);
			}
		} finally {
			LOG.trace("END: [" + name + "] " + schema);
		}
	}

	public String toString() {
		return getClass().getSimpleName() + " <" + name + "> [" + schema + "]";
	}
}
