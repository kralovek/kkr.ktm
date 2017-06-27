package kkr.ktm.components.lancer.infotel_executesql;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.components.lancer.Lancer;
import kkr.ktm.components.lancer.m2o_importfile.TestOutputImpl;
import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.database.UtilsDatabase;

public class LancerInfotelExecuteSql extends LancerInfotelExecuteSqlFwk implements Lancer {
	private static final Logger LOG = Logger.getLogger(LancerInfotelExecuteSql.class);

	private static final String PARAM_DATABASE = "DATABASE";

	private static final String PARAM_EXCEPTION_CLASS = "EXCEPTION/CLASS";

	private static final String PARAM_EXCEPTION_MESSAGE = "EXCEPTION/MESSAGE";

	private static final String PARAM_EXCEPTION_DETAIL = "EXCEPTION/DETAIL";

	private static final String PARAM_QUERY = "QUERY";

	private static final String PARAM_TEST_ID = "TEST_ID";

	public TestOutput lance(TestInput testInput, Map<String, Object> commonData) throws BaseException {
		LOG.trace("BEGIN");
		try {
			TestOutputImpl testOutput = new TestOutputImpl(testInput.getSource(), testInput.getType(), testInput.getId());

			try {
				Map<String, Object> inputParameters = createInputParameters(testInput);

				String templateData = templateArchiv.loadTemplate(testInput.getType());

				String query = templateParser.parse(templateData, inputParameters);

				testOutput.getDataOutput().put(sysParamPrefix + PARAM_QUERY, query);

				Map<String, Object> parametersOutput = executeQuery(query);

				testOutput.getDataOutput().putAll(parametersOutput);

			} catch (Throwable ex) {
				testOutput.getDataOutput().put(sysParamPrefix + PARAM_EXCEPTION_CLASS, ex.getClass().getName());
				testOutput.getDataOutput().put(sysParamPrefix + PARAM_EXCEPTION_MESSAGE, ex.getMessage());
				testOutput.getDataOutput().put(sysParamPrefix + PARAM_EXCEPTION_DETAIL, toStringException(ex));
			}

			LOG.trace("OK");
			return testOutput;
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, Object> executeQuery(String query) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<String, Object> retval = new HashMap<String, Object>();

			Connection connection = null;
			try {
				Statement statement = null;
				ResultSet resultSet = null;
				try {
					connection = dataSource.getConnection();
					try {
						statement = connection.createStatement();

						resultSet = statement.executeQuery(query);

						ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

						while (resultSet.next()) {
							for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
								String name = resultSetMetaData.getColumnName(i).toUpperCase();
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
											value = resultSet.getString(i);
									}
								}
								retval.put(name, value);
							}
						}

						resultSet.close();
						resultSet = null;

						statement.close();
						statement = null;
					} catch (SQLException ex) {
						throw new TechnicalException("Cannot execute the query: " + query, ex);
					}
				} finally {
					UtilsDatabase.getInstance().closeResource(statement);
				}

				connection.close();
				connection = null;
			} catch (SQLException ex) {
				throw new TechnicalException("Cannot connect to database", ex);
			} finally {
				UtilsDatabase.getInstance().closeResource(connection);
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, Object> createInputParameters(TestInput pTestUnit) {
		final Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put(PARAM_TEST_ID, pTestUnit.getId());
		inputParameters.putAll(pTestUnit.getDataInput());
		return inputParameters;
	}

	private String toStringException(Throwable pException) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(byteArrayOutputStream);
		pException.printStackTrace(printStream);
		printStream.close();
		return byteArrayOutputStream.toString();
	}
}
