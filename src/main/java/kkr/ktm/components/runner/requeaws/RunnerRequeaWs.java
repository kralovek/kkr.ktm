package kkr.ktm.components.runner.requeaws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import kkr.ktm.components.runner.Runner;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsFile;

public class RunnerRequeaWs extends RunnerRequeaWsFwk implements Runner {
	private static final Logger LOG = Logger.getLogger(RunnerRequeaWs.class);

	private static final String PARAM_SOAP_PATH = "SOAP_PATH";
	private static final String PARAM_SOAP_HOST = "SOAP_HOST";
	private static final String PARAM_SOAP_PORT = "SOAP_PORT";
	private static final String PARAM_SOAP_LENGTH = "SOAP_LENGTH";
	private static final String PARAM_SOAP_ENCODING = "SOAP_ENCODING";

	private static final String PARAM_RUNNER_REQUEST = "REQUEST";
	private static final String PARAM_RUNNER_RESPONSE = "RESPONSE";

	private static class TrustManagerInst implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] pCerts, String pAuthType) {
		}

		public void checkServerTrusted(X509Certificate[] pCerts, String pAuthType) {
		}
	}

	private static class HostnameVerifierInst implements HostnameVerifier {
		public boolean verify(String pArg0, SSLSession pArg1) {
			return true;
		}
	}

	/**
	 * Read/Write pause
	 */
	private static final int PAUSE = 10;

	/**
	 * Read buffer size
	 */
	private static final int BUFFER_SIZE = 50;

	public Map<String, Object> run(Map<String, Object> parameters) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Date date = new Date();

			Map<String, Object> allInputParameters = new HashMap<String, Object>();
			Map<String, Object> allOputParameters = new HashMap<String, Object>();

			allInputParameters.putAll(this.parameters);
			allInputParameters.putAll(parameters);

			if (!allInputParameters.containsKey(PARAM_SOAP_PATH)) {
				allInputParameters.put(PARAM_SOAP_PATH, url.getPath());
			}
			if (!allInputParameters.containsKey(PARAM_SOAP_HOST)) {
				allInputParameters.put(PARAM_SOAP_HOST, url.getHost());
			}
			if (!allInputParameters.containsKey(PARAM_SOAP_PORT)) {
				allInputParameters.put(PARAM_SOAP_PORT, url.getPort());
			}
			if (!allInputParameters.containsKey(PARAM_SOAP_ENCODING)) {
				allInputParameters.put(PARAM_SOAP_ENCODING, encoding);
			}

			String templateBody = templateArchiv.loadTemplate(templateBodyName);
			String parsedBody = formatterParameters.format(templateBody, allInputParameters);

			allInputParameters.put(PARAM_SOAP_LENGTH, parsedBody.length());

			String templateHeader = loadTemplate(templateHeadName);
			String parsedHeader = formatterParameters.format(templateHeader, allInputParameters);

			generateTraceFileRequest(parsedHeader, parsedBody, date);

			allOputParameters.put(sysParamPrefix + PARAM_RUNNER_REQUEST, parsedHeader + parsedBody);

			String response = callWebService(parsedHeader, parsedBody);

			generateTraceFileResponse(response, date);

			allOputParameters.put(sysParamPrefix + PARAM_RUNNER_RESPONSE, response);

			Map<String, Object> resultParameters = resultParser.parse(response);

			allOputParameters.putAll(resultParameters);

			LOG.trace("OK");
			return allOputParameters;
		} finally {
			LOG.trace("END");
		}
	}

	private String loadTemplate(String templateName) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String template = templateArchiv.loadTemplate(templateName);
			LOG.trace("OK");
			return template;
		} finally {
			LOG.trace("END");
		}
	}

	private String callWebService(final String header, final String body) throws BaseException {
		LOG.trace("BEGIN");
		try {
			final String hostname = url.getHost();
			int port = url.getPort();
			final String protocol = url.getProtocol();

			LOG.info("WS-CALL: URL: " + url.toExternalForm());

			if (port == -1) {
				port = 80;
				LOG.debug("The port is not set. The default value " + port + " is used");
			}

			Socket socket = null;
			try {
				if ("http".equals(protocol)) {
					socket = openSocketHttp(hostname, port);
				} else if ("https".equals(protocol)) {
					socket = openSocketHttps(hostname, port);
				} else {
					// already checked
				}

				try {
					final BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));

					bufferWriter.write(header);
					bufferWriter.write(body);
					bufferWriter.flush();
					// NO CLOSE !!!
				} catch (final IOException ex) {
					LOG.error(ex);
					throw new TechnicalException("Problem to connect to the remote service: " + url.toString(), ex);
				}

				final String content = readAnswer(socket, protocol);

				socket.close();
				socket = null;

				LOG.info("WS-RETURN");

				LOG.trace("OK");
				return content;
			} catch (final IOException ex) {
				throw new TechnicalException("Problem to communicate witch the remote service: " + url.toString(), ex);
			} finally {
				closeRessource(socket);
			}
		} finally {
			LOG.trace("END");
		}
	}

	private Socket openSocketHttp(final String pHostname, final int pPort) throws BaseException {
		LOG.trace("BEGIN");
		try {
			final InetAddress inetAddress = InetAddress.getByName(pHostname);
			Socket socket = new Socket(inetAddress, pPort);
			LOG.trace("OK");
			return socket;
		} catch (Exception ex) {
			LOG.error(ex);
			throw new TechnicalException("Cannot open the HTTP connection to " + pHostname + ":" + pPort, ex);
		} finally {
			LOG.trace("END");
		}
	}

	private Socket openSocketHttps(final String pHostname, final int pPort) throws BaseException {
		LOG.trace("BEGIN");
		try {
			final X509TrustManager trustManager = new TrustManagerInst();
			final TrustManager[] trustManagers = new TrustManager[]{trustManager};

			final HostnameVerifier hostnameVerifier = new HostnameVerifierInst();

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManagers, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			SSLSocketFactory ssf = HttpsURLConnection.getDefaultSSLSocketFactory();
			Socket socket = ssf.createSocket(pHostname, pPort);
			((SSLSocket) socket).startHandshake();
			LOG.trace("OK");
			return socket;
		} catch (Exception ex) {
			LOG.error(ex);
			throw new TechnicalException("Cannot open the HTTPS connection to " + pHostname + ":" + pPort, ex);
		} finally {
			LOG.trace("END");
		}
	}

	private String readAnswer(final Socket pSocket, final String pProtocol) throws BaseException {
		LOG.trace("BEGIN");
		InputStream responseInputStream = null;
		try {
			responseInputStream = pSocket.getInputStream();
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final byte[] byteBuffer = new byte[BUFFER_SIZE];

			cleanBuffer(byteBuffer);
			int readChars;
			while ((readChars = responseInputStream.read(byteBuffer)) != -1) {
				if (!writeAndContinue(byteArrayOutputStream, byteBuffer, readChars, BUFFER_SIZE, pProtocol)) {
					break;
				}
				cleanBuffer(byteBuffer);
			}

			responseInputStream.close();
			responseInputStream = null;

			byte[] bytes = byteArrayOutputStream.toByteArray();

			// Clean bytes
			bytes = cleanBytes(bytes);

			final String responseEncoding = findResponseEncoding(bytes);
			final String content = responseEncoding != null ? new String(bytes, responseEncoding) : new String(bytes);
			LOG.trace("OK");
			return content;
		} catch (final IOException ex) {
			throw new TechnicalException("Problem to receive the answer from the remote service: " + url.toString(), ex);
		} finally {
			closeRessource(responseInputStream);
			LOG.trace("END");
		}
	}

	private void closeRessource(final Closeable pCloseable) {
		if (pCloseable != null) {
			try {
				pCloseable.close();
			} catch (IOException ex) {
			}
		}
	}

	private void closeRessource(final Socket pSocket) {
		if (pSocket != null) {
			try {
				pSocket.close();
			} catch (IOException ex) {
			}
		}
	}

	private void cleanBuffer(final byte[] pBuffer) {
		for (int i = 0; i < pBuffer.length; i++) {
			pBuffer[i] = 0;
		}
	}

	/**
	 * Retourne FALSE s'il faut s'arreter
	 * 
	 * @param pByteArrayOutputStream
	 * @param pBuffer
	 * @param pReadChars
	 * @param pExpectedChars
	 * @return
	 */
	private boolean writeAndContinue(final ByteArrayOutputStream pByteArrayOutputStream, final byte[] pBuffer, final int pReadChars,
			final int pExpectedChars, final String pProtocol) {
		try {
			Thread.sleep(PAUSE);
		} catch (InterruptedException ex) {
		}
		pByteArrayOutputStream.write(pBuffer, 0, pReadChars);

		if (pReadChars == pExpectedChars) {
			return true;
		}
		if (!"https".equals(pProtocol)) {
			return false;
		}
		if (pReadChars > 2) {
			return (pBuffer[pReadChars - 2] == 0x0D && pBuffer[pReadChars - 1] == 0x0A);
		}
		return true;
	}

	private byte[] cleanBytes(final byte[] pBytes) {
		byte[] buffer = new byte[pBytes.length * 2];
		int cB = 0;
		for (int iS = 0; iS < pBytes.length;) {
			//
			// Removes the character xE2x80x98 = &lsquo; Left Single Quotation
			// Mark
			// Replace it by &apos;
			//
			if (iS + 2 < pBytes.length && pBytes[iS + 0] == (byte) 0xE2 && pBytes[iS + 1] == (byte) 0x80 && pBytes[iS + 2] == (byte) 0x99) {
				if (cB + 3 >= buffer.length) {
					buffer = extendBuffer(buffer);
				}
				buffer[cB++] = '&';
				buffer[cB++] = 'a';
				buffer[cB++] = 'p';
				buffer[cB++] = 'o';
				buffer[cB++] = 's';
				buffer[cB++] = ';';
				iS += 3;
			} else {
				buffer[cB++] = pBytes[iS++];
			}
		}
		return Arrays.copyOf(buffer, cB);
	}

	private byte[] extendBuffer(final byte[] pBytes) {
		byte[] retval = new byte[pBytes.length * 2];
		for (int i = 0; i < pBytes.length; i++) {
			retval[i] = pBytes[i];
		}
		return retval;
	}

	private String findResponseEncoding(final byte[] pBytes) {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(pBytes)));

		try {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					break;
				}
				if (line.startsWith("Content-Type:")) {
					int iPos = line.indexOf("charset=");
					if (iPos == -1) {
						continue;
					}
					String charset = line.substring(iPos + "charset=".length());
					iPos = charset.indexOf(" ");
					if (iPos != -1) {
						charset = charset.substring(0, iPos);
					}
					if (!charset.isEmpty()) {
						return charset;
					}
				}
			}
			return null;
		} catch (final IOException ex) {
			return null;
		}
	}

	private void generateTraceFileRequest(String requestHeader, String requestBody, Date date) throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (traceRequestPattern != null) {
				String path = traceRequestPattern.format(date);
				File file = new File(path);
				LOG.debug("Logging the REQUEST to: " + file.toString());
				if (file.getParentFile() != null && !file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
					throw new TechnicalException("Cannot create the directory: " + file.getParentFile().getAbsolutePath());
				}
				UtilsFile.getInstance().contentToFile(requestHeader + requestBody, file);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void generateTraceFileResponse(String response, Date date) throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (traceResponsePattern != null) {
				String path = traceResponsePattern.format(date);
				File file = new File(path);
				LOG.debug("Logging the RESPONSE to: " + file.toString());
				if (file.getParentFile() != null && !file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
					throw new TechnicalException("Cannot create the directory: " + file.getParentFile().getAbsolutePath());
				}
				UtilsFile.getInstance().contentToFile(response, file);
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
