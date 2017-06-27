package kkr.ktm.components.executant.webservice.requeaws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import kkr.ktm.components.executant.Executant;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;


public class ExecutantWebServiceRequea extends ExecutantWebServiceRequeaFwk implements Executant {
	private static final Logger LOG = Logger
			.getLogger(ExecutantWebServiceRequea.class);

	/**
	 * Read/Write pause
	 */
	private static final int PAUSE = 10;

	/**
	 * Read buffer size
	 */
	private static final int BUFFER_SIZE = 50;

	private static class TrustManagerInst implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] pCerts,
				String pAuthType) {
		}

		public void checkServerTrusted(X509Certificate[] pCerts,
				String pAuthType) {
		}
	}

	private static class HostnameVerifierInst implements HostnameVerifier {
		public boolean verify(String pArg0, SSLSession pArg1) {
			return true;
		}
	}

	
	public String execute(String pSource) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			String retval = callWebService(pSource);
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private String callWebService(final String pData) throws BaseException {
		final String path = url.getPath();
		final String hostname = url.getHost();
		int port = url.getPort();
		final String protocol = url.getProtocol();

		LOG.info("URL: " + url.toExternalForm());

		if (port == -1) {
			port = 80;
			LOG.debug("The ftpPort is not set. The default value " + port
							+ " is used");
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
				final BufferedWriter bufferWriter = new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream(),
								encoding)); // ENCODE_UTF_8));

				bufferWriter.write("POST " + path + " HTTP/1.1\r\n");
				bufferWriter.write("Accept-Encoding: gzip,deflate\r\n");
				bufferWriter
				.write("Content-Type: text/xml;charset=UTF-8\r\n");

				bufferWriter.write("SOAPAction: \"m2oKKRWS#myop\"\r\n");

				bufferWriter.write("User-Agent: Java/1.6.0_05\r\n");
				bufferWriter.write("Host: " + hostname + ":" + port + "\r\n");
				bufferWriter.write("Connection: keep-alive\r\n");
				bufferWriter.write("Content-Length: " + pData.length()
						+ "\r\n");

				bufferWriter.write("\r\n");

				bufferWriter.write(pData);
				bufferWriter.flush();
				// NO CLOSE !!!
			} catch (final IOException ex) {
				throw new TechnicalException(
						"Problem to connect to the remote service: "
								+ url.toString(), ex);
			}

			final String content = readAnswer(socket, protocol);

			socket.close();
			socket = null;
			return content;
		} catch (final IOException ex) {
			throw new TechnicalException(
					"Problem to communicate witch the remote service: "
							+ url.toString(), ex);
		} finally {
			closeRessource(socket);
		}
	}

	private Socket openSocketHttp(final String pHostname, final int pPort)
			throws BaseException {
		try {
			final InetAddress inetAddress = InetAddress.getByName(pHostname);
			Socket socket = new Socket(inetAddress, pPort);
			return socket;
		} catch (Exception ex) {
			throw new TechnicalException("Cannot open the HTTP connection to "
					+ pHostname + ":" + pPort, ex);
		}
	}

	private Socket openSocketHttps(final String pHostname, final int pPort)
			throws BaseException {
		try {
			final X509TrustManager trustManager = new TrustManagerInst();
			final TrustManager[] trustManagers = new TrustManager[] { trustManager };

			final HostnameVerifier hostnameVerifier = new HostnameVerifierInst();

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManagers, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			SSLSocketFactory ssf = HttpsURLConnection
					.getDefaultSSLSocketFactory();
			Socket socket = ssf.createSocket(pHostname, pPort);
			((SSLSocket) socket).startHandshake();
			return socket;
		} catch (Exception ex) {
			throw new TechnicalException("Cannot open the HTTPS connection to "
					+ pHostname + ":" + pPort, ex);
		}
	}

	private String readAnswer(final Socket pSocket, final String pProtocol)
			throws BaseException {
		InputStream responseInputStream = null;
		try {
			responseInputStream = pSocket.getInputStream();
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final byte[] byteBuffer = new byte[BUFFER_SIZE];

			cleanBuffer(byteBuffer);
			int readChars;
			while ((readChars = responseInputStream.read(byteBuffer)) != -1) {
				if (!writeAndContinue(byteArrayOutputStream, byteBuffer,
						readChars, BUFFER_SIZE, pProtocol)) {
					break;
				}
				cleanBuffer(byteBuffer);
				readChars = responseInputStream.read(byteBuffer);
			};

			responseInputStream.close();
			responseInputStream = null;

			byte[] bytes = byteArrayOutputStream.toByteArray();

			// Clean bytes
			bytes = cleanBytes(bytes);

			final String responseEncoding = findResponseEncoding(bytes);
			final String content = responseEncoding != null ? new String(bytes,
					responseEncoding) : new String(bytes);

			return content;
		} catch (final IOException ex) {
			throw new TechnicalException(
					"Problem to receive the answer from the remote service: "
							+ url.toString(), ex);
		} finally {
			closeRessource(responseInputStream);
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
	private boolean writeAndContinue(
			final ByteArrayOutputStream pByteArrayOutputStream,
			final byte[] pBuffer, final int pReadChars,
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
			if (iS + 2 < pBytes.length && pBytes[iS + 0] == (byte) 0xE2
					&& pBytes[iS + 1] == (byte) 0x80
					&& pBytes[iS + 2] == (byte) 0x99) {
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
		final BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(pBytes)));

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
}
