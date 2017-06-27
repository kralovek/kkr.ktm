package kkr.ktm.utils;


import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TechnicalException;

public class UtilsFile {

	private static UtilsFile utilsFile = new UtilsFile();

	public static final UtilsFile getInstance() {
		return utilsFile;
	}

	public String loadFileContent(final File pFile) throws BaseException {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(pFile);
			final char[] buffer = new char[(int) pFile.length()];
			fileReader.read(buffer);
			final String content = new String(buffer);
			fileReader.close();
			fileReader = null;
			return content;
		} catch (final FileNotFoundException ex) {
			throw new ConfigurationException("Template file does not exist: "
					+ pFile.getAbsolutePath());
		} catch (final IOException ex) {
			throw new TechnicalException("Cannot read the template file: "
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

	public void contentToFile(final String pContent, final File pFile)
			throws BaseException {
		FileOutputStream fileOutputStream = null;
		try {
			createFileDirectory(pFile);
			fileOutputStream = new FileOutputStream(pFile);
			if (pContent != null) {
				fileOutputStream.write(pContent.getBytes());
			}
			fileOutputStream.close();
			fileOutputStream = null;
		} catch (final IOException ex) {
			throw new TechnicalException("Cannot crate the file: "
					+ pFile.getAbsolutePath());
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (final IOException ex) {
				}
			}
		}
	}

	public void moveFile(File fileSource, File fileTarget) throws BaseException {
		createFileDirectory(fileTarget);
		fileSource.renameTo(fileTarget);
	}
	
	public void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ex) {
				// nothing to do
			}
		}
	}
	
	public void createFileDirectory(File file) throws BaseException {
		final File dir = file.getParentFile();
		if (dir != null && !dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new TechnicalException("Cannot crate the directory: "
						+ dir.getAbsolutePath());
			}
		}
	}
}
