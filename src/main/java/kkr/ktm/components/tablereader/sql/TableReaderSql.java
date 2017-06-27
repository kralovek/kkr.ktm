package kkr.ktm.components.tablereader.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.components.tablereader.TableReader;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.database.UtilsDatabase;

public class TableReaderSql extends TableReaderSqlFwk implements TableReader {
	private static final Logger LOG = Logger.getLogger(TableReaderSql.class);

	public Collection<Map<String, Object>> readData(String query) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<Map<String, Object>> retval = new ArrayList<Map<String, Object>>();

			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				Statement statement = null;
				ResultSet resultSet = null;
				try {
					statement = connection.createStatement();

					LOG.info("QUERY: " + query);

					resultSet = statement.executeQuery(query);
					ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

					while (resultSet.next()) {
						Map<String, Object> dataLine = new LinkedHashMap<String, Object>();
						retval.add(dataLine);
						for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
							String name = resultSetMetaData.getColumnName(i);
							Object value = resultSet.getObject(i);

							if (value != null) {
								switch (resultSetMetaData.getColumnType(i)) {
									case Types.DATE :
									case Types.TIME :
									case Types.TIMESTAMP : {
										Date valueDate = resultSet.getTimestamp(i);
										value = valueDate;
										break;
									}
									case Types.DECIMAL :
									case Types.INTEGER : {
										value = resultSet.getObject(i);
										break;
									}
									case Types.NUMERIC :
									case Types.DOUBLE : {
										value = resultSet.getObject(i);
										break;
									}
									default :
										value = value.toString();
								}
							}

							dataLine.put(name, value);
						}
					}

					resultSet.close();
					resultSet = null;

					statement.close();
					statement = null;

				} catch (SQLException ex) {
					throw new TechnicalException("Execute the query: " + query, ex);
				} finally {
					UtilsDatabase.getInstance().closeResource(resultSet);
					UtilsDatabase.getInstance().closeResource(statement);
				}

				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException("Cannot connec the database: " + dataSource.getSchema(), ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(connection);
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
