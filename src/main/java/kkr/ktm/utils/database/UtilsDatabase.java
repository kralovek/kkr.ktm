package kkr.ktm.utils.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class UtilsDatabase {
	private static UtilsDatabase instance = new UtilsDatabase();

	public static final UtilsDatabase getInstance() {
		return instance;
	}

	public void closeResource(Connection resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Throwable ex) {
				// nothig to do
			}
		}
	}

	public void closeResource(Statement resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Throwable ex) {
				// nothig to do
			}
		}
	}

	public void closeResource(ResultSet resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Throwable ex) {
				// nothig to do
			}
		}
	}
}
