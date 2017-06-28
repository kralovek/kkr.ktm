package kkr.ktm.components.diffmanager.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import kkr.ktm.components.diffmanager.DiffManager;
import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.database.ConstantsDatabase;
import kkr.ktm.utils.database.UtilsDatabase;

public class DiffManagerDatabase extends DiffManagerDatabaseFwk implements DiffManager, ConstantsDatabase {

	private static final Logger LOG = Logger.getLogger(DiffManagerDatabase.class);

	private static final DateFormat DATE_FORMAT_TODATE = new SimpleDateFormat("yyyyMMdd HHmmss SSS");

	public List<DiffGroup> loadDiffs(List<DiffGroup> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<DiffGroup> diffGroups = new ArrayList<DiffGroup>();

			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				for (TableInfo tableInfo : tableInfos) {
					DiffGroup groupState = findGroup(groupStates, tableInfo.getName());

					IndexImpl lastIndexImpl = (IndexImpl) groupState.getLastIndex();

					long date = groupState != null ? lastIndexImpl.getMs() : 0L;
					DiffGroup diffGroup = readDiffTable(connection, tableInfo, date);
					diffGroups.add(diffGroup);
				}

				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(connection);
			}
			LOG.trace("OK");
			return diffGroups;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffGroup findGroup(List<DiffGroup> diffGroups, String name) {
		for (DiffGroup diffGroup : diffGroups) {
			if (name.equals(diffGroup.getName())) {
				return diffGroup;
			}
		}
		return null;
	}

	public List<DiffGroup> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			List<DiffGroup> diffGroups = new ArrayList<DiffGroup>();

			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				// VDate dateCurrentTimestamp = loadCurrentTimestamp(connection);

				for (TableInfo tableInfo : tableInfos) {
					DiffGroup diffGroup = readCurrentTable(connection, tableInfo);
					diffGroups.add(diffGroup);
				}

				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(connection);
			}
			LOG.trace("OK");
			return diffGroups;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffGroup readDiffTable(Connection connection, TableInfo tableInfo, long index) throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<ItemCruid> groupCruid = new ArrayList<ItemCruid>();
			PreparedStatement statement = null;
			ResultSet resultSet = null;
			try {
				int typeSort = determineSortType(connection, tableInfo.getName(), tableInfo.getColumnSort());

				GroupImpl group = new GroupImpl(tableInfo.getName());
				boolean columnsListed = tableInfo.getColumns() != null && !tableInfo.getColumns().isEmpty();

				String query = null;
				if (columnsListed) {
					StringBuffer columns = new StringBuffer();
					for (String column : tableInfo.getColumns()) {
						columns.append(", ").append(column);
					}
					query = "" //
							+ " SELECT " + tableInfo.getColumnName() + ", " + tableInfo.getColumnSort() + columns //
							+ " FROM " + tableInfo.getName();
				} else {
					query = "" //
							+ " SELECT * " //
							+ " FROM " + tableInfo.getName();
				}

				String columnsSortMore = "";
				for (String columnSortMore : tableInfo.getColumnsSortMore()) {
					columnsSortMore += "," + columnSortMore + " ASC";
				}

				query += " WHERE " + tableInfo.getColumnSort() + " > ?" //
						+ " ORDER BY " + tableInfo.getColumnSort() + " ASC " + columnsSortMore; //
				statement = connection.prepareStatement(query);

				String queryRep = query;

				switch (typeSort) {
					case Types.TIMESTAMP :
					case Types.DATE :
					case Types.TIME : {
						Timestamp timestamp = new Timestamp(index);
						statement.setTimestamp(1, timestamp);
						queryRep = query.replaceFirst("\\?", toStringSqlDate(index));
						break;
					}
					case Types.NUMERIC :
					case Types.INTEGER :
					case Types.DECIMAL : {
						statement.setLong(1, index);
						queryRep = query.replaceFirst("\\?", "'" + index + "'");
						break;
					}
					default : {
						throw new ConfigurationException("The sortColumn " + tableInfo.getName() + "." + tableInfo.getColumnSort()
								+ " is not timestamp or a decimal number: type=" + typeSort);
					}
				}

				LOG.debug("QUERY: " + queryRep);

				resultSet = statement.executeQuery();
				ResultSetMetaData metaData = resultSet.getMetaData();

				while (resultSet.next()) {
					ItemCruid itemCruid = new ItemCruid(patternDate);
					itemCruid.setName(resultSet.getString(tableInfo.getColumnName()));

					long indexSort = 0;

					switch (typeSort) {
						case Types.TIMESTAMP :
						case Types.DATE :
						case Types.TIME : {
							Date dateSort = resultSet.getTimestamp(tableInfo.getColumnSort());
							dateSort = roundDateToMs(dateSort);
							indexSort = dateSort.getTime();
							break;
						}
						default : {
							indexSort = resultSet.getLong(tableInfo.getColumnSort());
						}
					}

					IndexImpl indexSortImpl = new IndexImpl();
					indexSortImpl.setMs(indexSort);
					itemCruid.setIndex(indexSortImpl);

					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						String columnName = metaData.getColumnName(i);

						switch (metaData.getColumnType(i)) {
							case Types.DATE :
							case Types.TIME :
							case Types.TIMESTAMP : {
								Date valueDate = resultSet.getTimestamp(i);
								itemCruid.getParameters().put(columnName, valueDate);
								break;
							}
							case Types.DECIMAL :
							case Types.INTEGER : {
								Object object = resultSet.getObject(i);
								itemCruid.getParameters().put(columnName, object);
								break;
							}
							case Types.NUMERIC :
							case Types.DOUBLE : {
								Object object = resultSet.getObject(i);
								itemCruid.getParameters().put(columnName, object);
								break;
							}
							default :
								String value = resultSet.getString(i);
								itemCruid.getParameters().put(columnName, value);
						}
					}
					groupCruid.add(itemCruid);

					if (tableInfo.getCheckerStatus() != null) {
						DiffStatus diffStatus = tableInfo.getCheckerStatus().checkStatus(index, itemCruid);
						itemCruid.setStatus(diffStatus);
					} else {
						itemCruid.setStatus(DiffStatus.NEW);
					}
				}

				LOG.debug("Items: " + groupCruid.size());

				if (tableInfo.getComparator() != null) {
					Collections.sort(groupCruid, tableInfo.getComparator());
				}

				for (ItemCruid itemCruid : groupCruid) {
					DiffItem diffItem = itemCruid.toItem();
					group.getItems().add(diffItem);
				}

				resultSet.close();
				statement.close();
				statement = null;

				LOG.trace("OK");
				return group;
			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(resultSet);
				UtilsDatabase.getInstance().closeResource(statement);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private DiffGroup readCurrentTable(Connection connection, TableInfo tableInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Statement statement = null;
			ResultSet resultSet = null;
			String query = "SELECT max(" + tableInfo.getColumnSort() + ") AS LAST_MODIFIED FROM " + tableInfo.getName();
			try {
				int typeSort = determineSortType(connection, tableInfo.getName(), tableInfo.getColumnSort());

				GroupImpl group = new GroupImpl(tableInfo.getName());

				LOG.debug("QUERY: " + query);

				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);

				if (resultSet.next()) {
					long index = 0;

					switch (typeSort) {
						case Types.TIMESTAMP :
						case Types.DATE :
						case Types.TIME : {
							Date date = resultSet.getTimestamp("LAST_MODIFIED");
							if (date != null) {
								date = roundDateToMs(date);
								LOG.debug("Value sort: " + DATE_FORMAT_TODATE.format(date));
								index = date.getTime();
							} else {
								LOG.debug("Table is empty");
								index = 0;
							}
							break;
						}
						case Types.NUMERIC :
						case Types.INTEGER :
						case Types.DECIMAL : {
							Object object = resultSet.getObject("LAST_MODIFIED");
							if (object != null) {
								index = resultSet.getLong("LAST_MODIFIED");
								LOG.debug("Value sort: " + index);
							} else {
								LOG.debug("Table is empty");
								index = 0;
							}
							break;
						}
						default : {
							throw new ConfigurationException("The sortColumn " + tableInfo.getName() + "." + tableInfo.getColumnSort()
									+ " is not timestamp or a decimal number: type=" + typeSort);
						}
					}

					IndexImpl indexImpl = new IndexImpl();
					indexImpl.setMs(index);

					group.setLastIndex(indexImpl);
				} else {
					throw new TechnicalException("No line in the result set: " + query);
				}

				resultSet.close();
				statement.close();
				statement = null;

				LOG.trace("OK");
				return group;
			} catch (SQLException ex) {
				if (ex.getErrorCode() == DB_ERROR_TABLE_NOT_EXIST) {
					throw new TechnicalException("The table does not exist: " + tableInfo.getName(), ex);
				} else {
					throw new TechnicalException("Cannot execute the statement: " + query, ex);
				}
			} finally {
				UtilsDatabase.getInstance().closeResource(resultSet);
				UtilsDatabase.getInstance().closeResource(statement);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private static Date roundDateToMs(Date date) {
		return new Date(date.getTime() + 1);
	}

	private int determineSortType(Connection connection, String table, String column) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String query = "SELECT " + column + " FROM " + table;
			Statement statement = null;
			ResultSet rs = null;
			try {
				int columnType = 0;
				statement = connection.createStatement();
				rs = statement.executeQuery(query);
				ResultSetMetaData rsm = rs.getMetaData();
				columnType = rsm.getColumnType(1);

				rs.close();
				rs = null;

				statement.close();
				statement = null;

				LOG.trace("OK");
				return columnType;
			} catch (SQLException ex) {
				if (ex.getErrorCode() == DB_ERROR_TABLE_NOT_EXIST) {
					throw new TechnicalException("The table does not existe: " + table, ex);
				} else if (ex.getErrorCode() == DB_ERROR_COLUMN_NOT_EXIST) {
					throw new TechnicalException("The column does not existe: " + table + "." + column, ex);
				}
				throw new TechnicalException("Cannot determinate the type of the column: " + table + "." + column, ex);
			} catch (Exception ex) {
				throw new TechnicalException("Cannot determinate the type of the column: " + table + "." + column, ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(statement);
				UtilsDatabase.getInstance().closeResource(rs);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private String toStringSqlDate(long ms) {
		return "to_timestamp('" + DATE_FORMAT_TODATE.format(new Date(ms)) + "','YYYYMMDD HH24MISS FF')";
	}
}
