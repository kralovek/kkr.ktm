package kkr.ktm.domains.common.components.diffmanager.database.trigger;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.errors.TechnicalException;
import kkr.common.utils.UtilsNumber;
import kkr.common.utils.UtilsResource;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;
import kkr.ktm.domains.common.components.diffmanager.database.data.DiffEntityImpl;
import kkr.ktm.domains.common.components.diffmanager.database.data.DiffItemImpl;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.utils.database.ConstantsDatabase;

public class DiffManagerDatabaseTrigger extends DiffManagerDatabaseTriggerFwk
		implements DiffManager, ConstantsDatabase {
	private static final Logger LOG = Logger.getLogger(DiffManagerDatabaseTrigger.class);

	private static final DateFormat DATE_FORMAT_TODATE = new SimpleDateFormat("yyyyMMdd HHmmss SSSSSS");
	private static final DateFormat DATE_FORMAT_STRING = new SimpleDateFormat("HH:mm:ss.SSSSSS dd/MM/yyyy");

	private static final String KTM_PREFIX_PK = "KTM_PK_";

	private static final char STATE_UNKNOWN = '-';
	private static final char STATE_INSERT = 'I';
	private static final char STATE_UPDATE = 'U';
	private static final char STATE_DELETE = 'D';
	private static final char STATE_IGNOR = 'X';

	private class PK implements Comparable<PK> {
		private List<String> pks;
		private List<DiffItemImpl> items = new ArrayList<DiffItemImpl>();

		public PK(List<String> pks) {
			this.pks = pks;
		}

		public List<DiffItemImpl> getItems() {
			return items;
		}

		private int compareToString(String s1, String s2) {
			return s1 != null && s2 != null ? s1.compareTo(s2) : s1 != null ? +1 : s2 != null ? -1 : 0;
		}

		public int compareTo(List<String> pks) {
			if (this.pks.size() != pks.size()) {
				return new Integer(this.pks.size()).compareTo(pks.size());
			}
			for (int i = 0; i < this.pks.size(); i++) {
				int retval = compareToString(this.pks.get(i), pks.get(i));
				if (retval != 0) {
					return retval;
				}
			}
			return 0;
		}

		public int compareTo(PK pk) {
			return compareTo(pk.pks);
		}

		public boolean equals(Object object) {
			if (!(object instanceof PK)) {
				return false;
			}
			return compareTo((PK) object) == 0;
		}

		public String toString() {
			return toStringValuesPK(pks);
		}
	}

	public Collection<DiffEntity> loadDiffs(Test test, Collection<DiffEntity> entities) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Collection<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				for (TableInfo tableInfo : tableInfos) {
					DiffEntity entityState = findEntity(entities, tableInfo.getName());
					DiffIndexImpl index = entityState != null ? (DiffIndexImpl) entityState.getLastIndex() : null;
					DiffEntity diffEntity = readDiffTable(connection, tableInfo, index);
					diffEntities.add(diffEntity);

					cleanTriggeredTable(index, tableInfo, connection);
				}

				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsResource.closeResource(connection);
			}
			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<DiffEntity> loadCurrents(Test test) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			List<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				for (TableInfo tableInfo : tableInfos) {
					DiffEntity diffEntity = readCurrentTable(connection, tableInfo);
					diffEntities.add(diffEntity);
				}

				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsResource.closeResource(connection);
			}
			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity findEntity(Collection<DiffEntity> diffEntities, String name) {
		name = adaptEntityName(name);
		for (DiffEntity diffEntity : diffEntities) {
			if (name.equals(diffEntity.getName())) {
				return diffEntity;
			}
		}
		return null;
	}

	private DiffEntity readCurrentTable(Connection connection, TableInfo tableInfo) throws BaseException {
		LOG.trace("BEGIN");
		String triggeredTableSchemaName = null;
		try {
			Statement statement = null;
			ResultSet resultSet = null;
			triggeredTableSchemaName = getTriggeredTableSchemaName(tableInfo);
			String query = "SELECT MAX(KTM_TS) FROM " + triggeredTableSchemaName;
			DiffEntityImpl entity = new DiffEntityImpl(adaptEntityName(tableInfo.getName()));

			try {
				LOG.debug("QUERY: " + query);

				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);

				Timestamp index = null;
				if (resultSet.next()) {
					try {
						index = resultSet.getTimestamp(1);
						LOG.debug("max ID: " + index);
					} catch (SQLException ex) {
						throw new TechnicalException(
								"The KTM_TS is NULL or is not a TIMESTAMP in the table: " + triggeredTableSchemaName);
					}
				}

				if (index == null) {
					// no line in the table
					index = new Timestamp(0);
				}

				DiffIndexImpl diffIndexImpl = new DiffIndexImpl(index);

				entity.setLastIndex(diffIndexImpl);

				resultSet.close();
				statement.close();
				statement = null;

				LOG.trace("OK");
				return entity;
			} catch (SQLException ex) {
				if (ex.getErrorCode() == DB_ERROR_TABLE_NOT_EXIST) {
					throw new TechnicalException("The table does not exist: " + triggeredTableSchemaName, ex);
				} else {
					throw new TechnicalException("Cannot execute the statement: " + query, ex);
				}
			} finally {
				UtilsResource.closeResource(resultSet);
				UtilsResource.closeResource(statement);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private String getTableSchemaName(TableInfo tableInfo) {
		return (tableInfo.getSchema() != null ? tableInfo.getSchema() + "." : "") + tableInfo.getName();
	}

	private String getTriggeredTableSchemaName(TableInfo tableInfo) {
		return (tableInfo.getKtmSchema() != null ? tableInfo.getKtmSchema() + "." : "") + tableInfo.getKtmNamePrefix()
				+ tableInfo.getName();
	}

	private void cleanTriggeredTable(DiffIndexImpl index, TableInfo tableInfo, Connection connection)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			PreparedStatement statement = null;

			String triggeredTableName = getTriggeredTableSchemaName(tableInfo);
			String query = "DELETE FROM " + triggeredTableName + " WHERE KTM_TS < ?";

			try {
				LOG.debug("QUERY: " + query);

				statement = connection.prepareStatement(query);
				statement.setTimestamp(1, index.getTimestamp());

				statement.executeUpdate();
				statement.close();

			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsResource.closeResource(statement);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity readDiffTable(Connection connection, TableInfo tableInfo, DiffIndexImpl index)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.debug("Table: " + tableInfo.getName());

			List<PK> listPks = new ArrayList<PK>();
			PreparedStatement statement = null;
			ResultSet resultSet = null;
			try {
				DiffEntityImpl entity = new DiffEntityImpl(adaptEntityName(tableInfo.getName()));
				entity.setLastIndex(index);

				String triggeredTableSchemaName = getTriggeredTableSchemaName(tableInfo);
				String tableSchemaName = getTableSchemaName(tableInfo);

				String sqlListPKs = toSqlListKtuPKs(tableInfo);

				String query = "" //
						+ "\n" + "SELECT * FROM (" //
						+ "\n" + "     SELECT KTM_STATE, KTM_TS, " + sqlListPKs //
						+ "\n" + "     FROM " + triggeredTableSchemaName //
						+ "\n" + "     WHERE KTM_TS > ?" + " ) tt" //
						+ "\n" + "LEFT JOIN " + tableSchemaName + " t ON " + toSqlListJoinKtuPKs(tableInfo, "t", "tt") //
						+ "\n" + "ORDER BY tt.KTM_TS ASC" //
				;

				String queryRep = query.replaceFirst("\\?", toString(index.getTimestamp()));

				statement = connection.prepareStatement(query);

				Timestamp indexTimestamp = index.getTimestamp();
				LOG.debug("timestamp: " + indexTimestamp);
				statement.setTimestamp(1, indexTimestamp);

				LOG.debug("QUERY: " + queryRep);

				resultSet = statement.executeQuery();
				ResultSetMetaData metaData = resultSet.getMetaData();

				while (resultSet.next()) {
					DiffItemImpl item = new DiffItemImpl();

					String state = resultSet.getString(1);
					DiffStatus diffStatus = castStatus(state);
					item.setStatus(diffStatus);

					Timestamp timestamp = resultSet.getTimestamp(2);
					DiffIndexImpl diffIndexImpl = new DiffIndexImpl(timestamp);
					item.setIndex(diffIndexImpl);

					List<String> valuesPK = new ArrayList<String>();

					for (int i = 3; i <= metaData.getColumnCount(); i++) {
						boolean isPK = false;
						String columnName = metaData.getColumnName(i);
						if (i <= 2 + tableInfo.getColumnsPK().size()) {
							columnName = columnName.substring(KTM_PREFIX_PK.length());
							isPK = true;
						} else {
							if (isPK(tableInfo, columnName)) {
								continue;
							}
						}

						Object valueFinal = resultSet.getObject(i);
						if (valueFinal != null) {
							switch (metaData.getColumnType(i)) {
							case Types.DATE:
							case Types.TIME:
							case Types.TIMESTAMP: {
								valueFinal = resultSet.getTimestamp(i);
								if (isPK) {
									valuesPK.add(valueFinal != null ? DATE_FORMAT_STRING.format(valueFinal) : null);
								}
								break;
							}
							case Types.DECIMAL:
							case Types.INTEGER:
							case Types.NUMERIC:
							case Types.DOUBLE: {
								Number number = (Number) resultSet.getObject(i);
								valueFinal = UtilsNumber.reduceNumber(number);
								if (isPK) {
									valuesPK.add(valueFinal != null ? valueFinal.toString() : null);
								}
								break;
							}
							case Types.BOOLEAN: {
								valueFinal = resultSet.getBoolean(i);
								if (isPK) {
									valuesPK.add(valueFinal != null ? valueFinal.toString() : null);
								}
								break;
							}
							default: {
								valueFinal = resultSet.getString(i);
								if (isPK) {
									valuesPK.add((String) valueFinal);
								}
							}
							}
						}

						item.getParameters().put(columnName, valueFinal);
					}
					item.setName(toStringValuesPK(valuesPK));

					PK pk = removeItemCruidsByPK(listPks, valuesPK);
					if (pk == null) {
						pk = new PK(valuesPK);
					}
					pk.getItems().add(item);
					listPks.add(pk);
				}

				if (tableInfo.getJoinStatus()) {
					entityByStatus(entity, listPks);
				} else {
					entityByResult(entity, listPks);
				}

				resultSet.close();
				statement.close();
				statement = null;

				LOG.trace("OK");
				return entity;
			} catch (SQLException ex) {
				throw new TechnicalException(ex);
			} finally {
				UtilsResource.closeResource(resultSet);
				UtilsResource.closeResource(statement);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private void entityByStatus(DiffEntityImpl group, List<PK> listPks) {
		for (PK pk : listPks) {
			if (pk.getItems().size() == 1) {
				DiffItem lastItem = pk.getItems().iterator().next();
				group.getItems().add(lastItem);
			} else {
				DiffStatus diffStatus = adaptStatuses(pk.getItems());
				if (diffStatus == null) { // line was added and removed after
					continue;
				}
				DiffItemImpl lastItem = pk.getItems().get(pk.getItems().size() - 1);
				lastItem.setStatus(diffStatus);
				group.getItems().add(lastItem);
			}
		}
	}

	private void entityByResult(DiffEntityImpl group, Collection<PK> listPks) {
		for (PK pk : listPks) {
			group.getItems().addAll(pk.getItems());
		}
	}

	private String toSqlListKtuPKs(TableInfo tableInfo) {
		StringBuffer buffer = new StringBuffer();
		for (String columnPK : tableInfo.getColumnsPK()) {
			if (buffer.length() != 0) {
				buffer.append(',');
			}
			buffer.append(KTM_PREFIX_PK).append(columnPK);
		}
		return buffer.toString();
	}

	private String toSqlListJoinKtuPKs(TableInfo tableInfo, String aliasTable, String aliasTriggeredTable) {
		StringBuffer buffer = new StringBuffer();
		for (String columnPK : tableInfo.getColumnsPK()) {
			if (buffer.length() != 0) {
				buffer.append(" AND ");
			}
			buffer.append(aliasTriggeredTable).append('.').append(KTM_PREFIX_PK).append(columnPK).append(" = ")
					.append(aliasTable).append('.').append(columnPK);
		}
		return buffer.toString();
	}

	private String toString(Timestamp timestamp) {
		return "to_timestamp('" + DATE_FORMAT_TODATE.format(timestamp) + "','YYYYMMDD HH24MISS FF')";
	}

	private boolean isPK(TableInfo tableInfo, String column) {
		for (String columnPK : tableInfo.getColumnsPK()) {
			if (column.equalsIgnoreCase(columnPK)) {
				return true;
			}
		}
		return false;
	}

	private static String toStringValuesPK(Collection<String> valuesPK) {
		if (valuesPK != null) {
			StringBuffer buffer = new StringBuffer();
			for (String valuePK : valuesPK) {
				if (buffer.length() != 0) {
					buffer.append('-');
				}
				buffer.append(valuePK);
			}
			return buffer.toString();
		} else {
			return "-";
		}
	}

	private DiffStatus castStatus(String status) {
		if (status == null || status.length() != 1) {
			return DiffStatus.UNK;
		}
		switch (status.charAt(0)) {
		case STATE_INSERT:
			return DiffStatus.NEW;
		case STATE_UPDATE:
			return DiffStatus.UPD;
		case STATE_DELETE:
			return DiffStatus.DEL;
		default:
			return DiffStatus.UNK;
		}
	}

	private DiffStatus adaptStatuses(Collection<DiffItemImpl> items) {
		StringBuffer buffer = new StringBuffer();
		for (DiffItem item : items) {
			if (item.getStatus().equals(DiffStatus.NEW)) {
				buffer.append(STATE_INSERT);
			} else if (item.getStatus().equals(DiffStatus.UPD)) {
				buffer.append(STATE_UPDATE);
			} else if (item.getStatus().equals(DiffStatus.DEL)) {
				buffer.append(STATE_DELETE);
			} else if (item.getStatus().equals(DiffStatus.UNK)) {
				buffer.append(STATE_UNKNOWN);
			}
		}
		String states = buffer.toString();

		char stateIni = states.charAt(0);
		char stateCur = stateIni;

		for (int i = 1; i < states.length(); i++) {
			char stateNew = states.charAt(i);

			switch (stateIni) {
			case STATE_INSERT:
				switch (stateCur) {
				case STATE_UNKNOWN:
					return DiffStatus.UNK;
				case STATE_INSERT:
					switch (stateNew) {
					case STATE_UPDATE:
						stateCur = STATE_INSERT;
						break;
					case STATE_DELETE:
						stateCur = STATE_IGNOR;
						break;
					default:
						return DiffStatus.UNK;
					}
					break;
				case STATE_IGNOR:
					switch (stateNew) {
					case STATE_INSERT:
						stateCur = STATE_INSERT;
						break;
					default:
						return DiffStatus.UNK;
					}
					break;
				default:
					return DiffStatus.UNK;
				}
				break;
			case STATE_UPDATE:
			case STATE_DELETE:
				switch (stateCur) {
				case STATE_UPDATE:
					switch (stateNew) {
					case STATE_UPDATE:
						stateCur = STATE_UPDATE;
						break;
					case STATE_DELETE:
						stateCur = STATE_DELETE;
						break;
					default:
						return DiffStatus.UNK;
					}
					break;
				case STATE_DELETE:
					switch (stateNew) {
					case STATE_INSERT:
						stateCur = STATE_UPDATE;
						break;
					default:
						return DiffStatus.UNK;
					}
					break;
				default:
					return DiffStatus.UNK;
				}
				break;
			default:
				return DiffStatus.UNK;
			}
		}

		switch (stateCur) {
		case STATE_IGNOR:
			return null;
		case STATE_INSERT:
			return DiffStatus.NEW;
		case STATE_UPDATE:
			return DiffStatus.UPD;
		case STATE_DELETE:
			return DiffStatus.DEL;
		}
		return null;
	}

	private PK removeItemCruidsByPK(List<PK> listPks, List<String> ValuesPk) {
		Iterator<PK> iterator = listPks.iterator();
		while (iterator.hasNext()) {
			PK pk = iterator.next();
			if (pk.compareTo(ValuesPk) == 0) {
				iterator.remove();
				return pk;
			}
		}
		return null;
	}

	private String adaptEntityName(String name) {
		if (UtilsString.isEmpty(code)) {
			return name;
		} else {
			return code + "." + name;
		}
	}

	public String toString() {
		return "[" + code + "]: " + UtilsString.toStringCollection(tableInfos, null, null, ",");
	}
}
