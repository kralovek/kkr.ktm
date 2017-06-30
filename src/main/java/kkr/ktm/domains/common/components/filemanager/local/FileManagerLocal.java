package kkr.ktm.domains.common.components.filemanager.local;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.filemanager.FileManager;
import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.errors.TechnicalException;

public class FileManagerLocal extends FileManagerLocalFwk implements FileManager {
	private static final Logger LOG = Logger
			.getLogger(FileManagerLocal.class);


	public void contentToFile(String content, String filename, String encoding,
			String dir) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			File dirTarget = new File(dir);
			File file = new File(dirTarget, filename);
			contentToFile(content, file, encoding);

			File fileTrace = generateTraceFile(content, new Date(), filename,
					encoding);
			if (fileTrace != null) {
				contentToFile(content, fileTrace, encoding);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void contentToGzFile(String content, String filename,
			String encoding, String dir) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			File dirTarget = new File(dir);
			File file = new File(dirTarget, filename);
			contentToGzFile(content, file, encoding);

			File fileTrace = generateTraceFile(content, new Date(), filename,
					encoding);
			if (fileTrace != null) {
				contentToGzFile(content, fileTrace, encoding);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void contentToFile(String content, File file, String encoding)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			try {
				fos = new FileOutputStream(file);
				if ("UTF-8".equals(encoding)) {
					writeBomUtf8(fos);
				}

				osw = new OutputStreamWriter(fos, encoding);

				osw.write(content);

				osw.close();
				osw = null;
				fos.close();
				fos = null;
			} catch (IOException ex) {
				throw new TechnicalException("Impossible to create the file: "
						+ file.getAbsolutePath(), ex);
			} finally {
				closeResource(osw);
				closeResource(fos);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void contentToGzFile(String content, File file, String encoding)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			GZIPOutputStream gzos = null;
			try {
				fos = new FileOutputStream(file);

				gzos = new GZIPOutputStream(fos);

				if ("UTF-8".equals(encoding)) {
					writeBomUtf8(gzos);
				}

				osw = new OutputStreamWriter(gzos, encoding);

				osw.write(content);

				osw.close();
				osw = null;
				gzos.close();
				gzos = null;
				fos.close();
				fos = null;
			} catch (IOException ex) {
				throw new TechnicalException("Impossible to create the file: "
						+ file.getAbsolutePath(), ex);
			} finally {
				closeResource(osw);
				closeResource(gzos);
				closeResource(fos);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private static void closeResource(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception ex) {
			}
		}
	}

	private static void writeBomUtf8(OutputStream fos) throws IOException {
		fos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
		fos.flush();
	}

	private File generateTraceFile(String content, Date date, String filename,
			String encoding) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File file = null;
			if (traceDataFile != null) {
				String traceDataFileAdapted = traceDataFile.replace("%0",
						filename);

				DateFormat traceDataPattern = null;
				try {
					traceDataPattern = new SimpleDateFormat(
							traceDataFileAdapted);
				} catch (Exception ex) {
					throw new ConfigurationException(getClass().getSimpleName()
							+ ": Parameter traceDataFile has bad value: "
							+ ex.getMessage());
				}
				String path = traceDataPattern.format(date);
				file = new File(path);
				LOG.debug("Logging the Data file to: " + file.toString());
				if (file.getParentFile() != null
						&& !file.getParentFile().isDirectory()
						&& !file.getParentFile().mkdirs()) {
					throw new TechnicalException(
							"Cannot create the directory: "
									+ file.getParentFile().getAbsolutePath());
				}

				contentToFile(content, file, encoding);
			}

			LOG.trace("OK");
			return file;
		} finally {
			LOG.trace("END");
		}
	}
}
