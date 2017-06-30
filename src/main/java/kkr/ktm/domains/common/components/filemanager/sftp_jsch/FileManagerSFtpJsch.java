package kkr.ktm.domains.common.components.filemanager.sftp_jsch;

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

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import kkr.ktm.domains.common.components.filemanager.FileManager;
import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.errors.TechnicalException;

public class FileManagerSFtpJsch extends FileManagerSFtpJschFwk implements FileManager {
	private static final Logger LOG = Logger.getLogger(FileManagerSFtpJsch.class);

	public void contentToFile(String content, String filename, String encoding, String dir) throws BaseException {
		contentToFile(content, filename, encoding, dir, false);
	}

	public void contentToGzFile(String content, String filename, String encoding, String dir) throws BaseException {
		contentToFile(content, filename, encoding, dir, true);
	}

	private void contentToFile(String content, String filename, String encoding, String dir, boolean gz) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			Session session = null;
			ChannelSftp sftpChannel = null;

			try {
				LOG.trace("Connecting to server: " + ftpHost);
				JSch jsch = new JSch();
				session = jsch.getSession(ftpLogin, ftpHost, ftpPort);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(ftpPassword);
				session.connect();

				Channel channel = session.openChannel("sftp");
				channel.connect();
				sftpChannel = (ChannelSftp) channel;

				LOG.trace("Connected");

				LOG.trace("Changing the directory to: " + dir);
				changeDirectory(sftpChannel, dir);

				InputStream inputStream = null;
				OutputStream outputStream = null;
				Writer writer = null;
				Reader reader = null;

				LOG.trace("Changed");

				try {
					LOG.trace("Sending the file: " + filename);
					try {
						inputStream = new ByteArrayInputStream(content.getBytes());

						reader = new BufferedReader(new InputStreamReader(inputStream, encoding), 8 * 1024);

					} catch (IOException ex) {
						throw new TechnicalException("Impossible to read the content string", ex);
					}

					try {
						outputStream = sftpChannel.put(filename);

						if (gz) {
							outputStream = new GZIPOutputStream(outputStream);
						}

						if ("UTF-8".equals(encoding)) {
							writeBomUtf8(outputStream);
						}

						writer = new BufferedWriter(new OutputStreamWriter(outputStream, encoding), 8 * 1024);
					} catch (IOException ex) {
						throw new TechnicalException("Impossible to create the file: " + filename + " in the remote directory: " + dir, ex);
					}

					try {
						copyFiles(reader, writer);
					} catch (IOException ex) {
						throw new TechnicalException("Impossible to copy the local file: " + filename + " to remote directory: " + dir);
					}

					LOG.trace("File sent");

					writer.flush();

					try {
						writer.close();
						writer = null;
					} catch (IOException ex) {
						LOG.error(ex);
						throw ex;
					}
					outputStream.close();
					outputStream = null;
					reader.close();
					reader = null;
					inputStream.close();
					inputStream = null;

				} finally {
					closeRessource(writer);
					closeRessource(outputStream);
					closeRessource(reader);
					closeRessource(inputStream);
				}

				if (traceDataFile != null) {
					File fileTrace = generateTraceFile(content, new Date(), filename, encoding);
					try {
						sftpChannel.get(filename, fileTrace.getAbsolutePath());
					} catch (SftpException ex) {
						throw new TechnicalException("Cannot create the trace file: " + fileTrace.getAbsolutePath(), ex);
					}
				}

				sftpChannel.disconnect();
				sftpChannel = null;
				session.disconnect();
				session = null;
			} catch (SftpException ex) {
				throw new TechnicalException("Impossible to create the file: " + filename + " in the remote directory: " + dir, ex);
			} catch (JSchException ex) {
				throw new TechnicalException("Impossible to create the file: " + filename + " in the remote directory: " + dir, ex);
			} catch (IOException ex) {
				throw new TechnicalException("Impossible to create the file: " + filename + " in the remote directory: " + dir, ex);
			} finally {
				if (sftpChannel != null) {
					sftpChannel.disconnect();
				}
				if (session != null) {
					session.disconnect();
				}
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void changeDirectory(ChannelSftp client, String dirRemote) throws BaseException {
		try {
			client.cd(dirRemote);
		} catch (SftpException ex) {
			throw new TechnicalException("Imposible to change the remote directory to: " + dirRemote, ex);
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

	private File generateTraceFile(String content, Date date, String filename, String encoding) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File file = null;
			if (traceDataFile != null) {
				String traceDataFileAdapted = traceDataFile.replace("%0", filename);

				DateFormat traceDataPattern = null;
				try {
					traceDataPattern = new SimpleDateFormat(traceDataFileAdapted);
				} catch (Exception ex) {
					throw new ConfigurationException(getClass().getSimpleName() + ": Parameter traceDataFile has bad value: " + ex.getMessage());
				}
				String path = traceDataPattern.format(date);
				file = new File(path);
				LOG.debug("Logging the Data file to: " + file.toString());
				if (file.getParentFile() != null && !file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
					throw new TechnicalException("Cannot create the directory: " + file.getParentFile().getAbsolutePath());
				}
			}

			LOG.trace("OK");
			return file;
		} finally {
			LOG.trace("END");
		}
	}

	private static void writeBomUtf8(OutputStream fos) throws IOException {
		fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
		fos.flush();
	}
}
