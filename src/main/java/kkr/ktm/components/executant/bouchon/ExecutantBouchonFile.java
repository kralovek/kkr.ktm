package kkr.ktm.components.executant.bouchon;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import kkr.ktm.components.executant.Executant;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.FunctionalException;
import kkr.ktm.exception.TechnicalException;

/**
 * ExecutantBouchonFile
 * 
 * @author KRALOVEC-99999
 */
public class ExecutantBouchonFile extends ExecutantBouchonFileFwk implements Executant {
	private static final Logger LOG = Logger
			.getLogger(ExecutantBouchonFile.class);

	public String execute(final String pSource) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			String contentXml = loadContentFromFile(file);
			LOG.trace("OK");
			return contentXml;
		} finally {
			LOG.trace("END");
		}
	}

	private String loadContentFromFile(final File pFile) throws BaseException {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(pFile);
			char[] buffer = new char[(int) pFile.length()];
			fileReader.read(buffer);
			String content = new String(buffer);
			fileReader.close();
			fileReader = null;
			return content;
		} catch (final FileNotFoundException ex) {
			throw new FunctionalException("Result file does not exist: "
					+ pFile.getAbsolutePath());
		} catch (final IOException ex) {
			throw new TechnicalException("Cannot read the Result file: "
					+ pFile.getAbsolutePath());
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (final IOException ex) {
				}
			}
		}
	}
}
