package kkr.ktm.components.locker.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import kkr.ktm.components.locker.Locker;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.database.UtilsDatabase;
import kkr.ktm.utils.errors.StopException;

public class LockerDatabase extends LockerDatabaseFwk implements Locker {
	private static final Logger LOG = Logger.getLogger(LockerDatabase.class);

	private static class Entity {
		private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		private String name;
		private long time;

		public Entity(String name, long time) {
			super();
			this.name = name;
			this.time = time;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return "[" + name + " " + dateFormat.format(new Date(time)) + "]";
		}
	}

	public void lock() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				insertLock(connection);

				List<Entity> waitings = null;

				long waited = 0;
				int lastCount = 0;
				boolean locked = false;
				do {
					waitings = selectWaitings(connection);

					if (waitings.get(0).getName().equals(whoami)) {
						locked = true;
						break;
					}

					if (lastCount != waitings.size()) {
						printWaitings(waitings);
					}

					try {
						Thread.sleep(waitInterval);
					} catch (InterruptedException ex) {
						throw new TechnicalException("Probleme to sleep the process", ex);
					}

					lastCount = waitings.size();

					waited += waitInterval;

				} while (waitMax == null || waited <= waitMax);

				connection.close();
				connection = null;

				if (!locked) {
					LOG.warn("STOPPING the proces - I cannot wait more");
					printWaitings(waitings);
					throw new StopException("No more waiting");
				}

				LOG.trace("OK");
			} catch (SQLException ex) {
				throw new TechnicalException("Cannot lock for me", ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(connection);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void unlock() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				String queryDelete = "delete from m2oScriptTester where m2oName = ?";

				PreparedStatement ps = null;
				try {
					ps = connection.prepareStatement(queryDelete);
					ps.setString(1, whoami);
					ps.executeUpdate();
					ps.close();
					ps = null;
				} catch (SQLException ex) {
					throw new TechnicalException("Cannot lock for me", ex);
				} finally {
					UtilsDatabase.getInstance().closeResource(ps);
				}
				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException("Cannot lock for me", ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(connection);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void insertLock(Connection connection) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String queryInsert = "insert into m2oScriptTester (SYSID, SYSVERSION, SYSUPDTIME) values (?, 0, now())";

			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(queryInsert);
				ps.setString(1, whoami);
				ps.executeUpdate();
				ps.close();
				ps = null;
			} catch (SQLException ex) {
				throw new TechnicalException("Cannot lock for me", ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(ps);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private List<Entity> selectWaitings(Connection connection) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String querySelect = "" //
					+ " select m2oName, sysupdtime " + " from m2oscripttester " + " where sysid <= (" + "     select sysupdtime "
					+ "     from m2oscripttester " + "     where m2oName = ?) " + " order by sysupdtime, m2oName";

			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(querySelect);
				ps.setString(1, whoami);
				rs = ps.executeQuery();

				List<Entity> waitings = new ArrayList<LockerDatabase.Entity>();
				while (rs.next()) {
					Entity entity = new Entity(rs.getString(1), rs.getTimestamp(2).getTime());
					waitings.add(entity);
				}

				rs.close();
				rs = null;

				ps.close();
				ps = null;

				LOG.trace("OK");
				return waitings;

			} catch (SQLException ex) {
				throw new TechnicalException("Cannot lock for me", ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(rs);
				UtilsDatabase.getInstance().closeResource(ps);
			}

		} finally {
			LOG.trace("END");
		}
	}

	private void printWaitings(List<Entity> waitings) {
		LOG.info("Waiting for: ");
		for (Entity entity : waitings) {
			LOG.info(entity.toString());
		}
	}
}
