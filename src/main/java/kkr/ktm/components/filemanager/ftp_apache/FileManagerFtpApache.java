package kkr.ktm.components.filemanager.ftp_apache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import kkr.ktm.components.filemanager.FileManager;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsFile;
import kkr.ktm.utils.ftp.UtilsFtp;

public class FileManagerFtpApache extends FileManagerFtpApacheFwk implements FileManager {
	private static final Logger LOG = Logger.getLogger(FileManagerFtpApache.class);

	public void contentToFile(String content, String filename, String encoding,
			String dir) throws BaseException {
		contentToFile(content, filename, encoding, dir, false);
	}

	public void contentToGzFile(String content, String filename,
			String encoding, String dir) throws BaseException {
		contentToFile(content, filename, encoding, dir, true);
	}

	private void contentToFile(String content, String filename,
			String encoding, String dir, boolean gz) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			generateTraceFile(content, new Date(), filename, encoding);

			FTPClient client = UtilsFtp.connect(ftpHost, ftpPort, ftpLogin,
					ftpPassword);

			try {
				changeDirectory(client, dir);

				try {
					if (!client.setFileType(FTP.BINARY_FILE_TYPE)) {
						throw new TechnicalException(
								"Impossible to change the tranfer type");
					}
				} catch (IOException ex) {
					throw new TechnicalException(
							"Impossible to change the tranfer type", ex);
				}

				InputStream inputStream = null;
				OutputStream outputStream = null;
				Writer writer = null;
				Reader reader = null;
				try {
					try {
						inputStream = new ByteArrayInputStream(
								content.getBytes());

						reader = new BufferedReader(new InputStreamReader(
								inputStream, encoding), 8 * 1024);

					} catch (IOException ex) {
						throw new TechnicalException(
								"Impossible to read the content string", ex);
					}

					try {
						outputStream = client.appendFileStream(filename);

						if (outputStream == null) {
							throw new TechnicalException(
									"Impossible to create the file: " + filename
											+ " in the remote directory: " + dir + ". Connection problem"
									);
						}

						if (gz) {
							outputStream = new GZIPOutputStream(outputStream);
						}

						if ("UTF-8".equals(encoding)) {
							writeBomUtf8(outputStream);
						}

						writer = new BufferedWriter(new OutputStreamWriter(
								outputStream, encoding), 8 * 1024);
					} catch (IOException ex) {
						throw new TechnicalException(
								"Impossible to create the file: " + filename
										+ " in the remote directory: " + dir,
								ex);
					}

					try {
						copyFiles(reader, writer);

						writer.close();
						writer = null;
						outputStream.close();
						outputStream = null;
						reader.close();
						reader = null;
						inputStream.close();
						inputStream = null;
					} catch (IOException ex) {
						throw new TechnicalException(
								"Impossible to copy the local file: "
										+ filename + " to remote directory: "
										+ dir);
					}
				} finally {
					closeRessource(writer);
					closeRessource(outputStream);
					closeRessource(reader);
					closeRessource(inputStream);
				}
			} finally {
				try {
					if (client != null) {
						client.disconnect();
					}
				} catch (IOException ex) {
					// rien � faire
				}
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void changeDirectory(FTPClient client, String dirRemote)
			throws BaseException {
		try {
			if (!client.changeWorkingDirectory(dirRemote)) {
				throw new TechnicalException(
						"The remote directory does not exist: " + dirRemote);
			}
		} catch (IOException ex) {
			throw new TechnicalException(
					"Imposible to change the remote directory to: " + dirRemote,
					ex);
		}
	}

	private void copyFiles(Reader reader, Writer writer) throws IOException {
		char chars[] = new char[1000];
		int count = 0;

		while ((count = reader.read(chars)) != -1) {
			writer.write(chars, 0, count);
		}
	}

	protected void closeRessource(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception ex) {
				// nothing to do
			}
		}
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

				UtilsFile.getInstance().contentToFile(content, file);
			}

			LOG.trace("OK");
			return file;
		} finally {
			LOG.trace("END");
		}
	}

	private static void writeBomUtf8(OutputStream fos) throws IOException {
		fos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
		fos.flush();
	}
}
