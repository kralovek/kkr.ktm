package kkr.ktm.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;



public class UtilsProperties {
	private static UtilsProperties utilsProperties = new UtilsProperties();

	public static UtilsProperties getInstance() {
		return utilsProperties;
	}

	public Properties loadPropertiesFromClasspath(final String pSource)
			throws BaseException {
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(pSource);

			Properties properties = new Properties();
			properties.load(inputStream);

			inputStream.close();
			inputStream = null;

			return properties;

		} catch (final IOException ex) {
			throw new TechnicalException(
					"Cannot leer the resource: " + pSource, ex);
		} finally {
			closeRessource(inputStream);
		}
	}

	public Properties loadPropertiesFromFile(final File pFile)
			throws BaseException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(pFile);

			Properties properties = new Properties();
			properties.load(fileInputStream);

			fileInputStream.close();
			fileInputStream = null;

			return properties;
		} catch (final FileNotFoundException ex) {
			throw new TechnicalException("The file does not exist: "
					+ pFile.getAbsolutePath(), ex);
		} catch (final IOException ex) {
			throw new TechnicalException("Cannot leer the file: "
					+ pFile.getAbsolutePath(), ex);
		} finally {
			closeRessource(fileInputStream);
		}
	}

	private void closeRessource(final Closeable pClosable) {
		if (pClosable != null) {
			try {
				pClosable.close();
			} catch (final Exception ex) {
			} finally {
			}
		}
	}
}
