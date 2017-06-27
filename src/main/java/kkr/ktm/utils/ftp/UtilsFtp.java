package kkr.ktm.utils.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;

public class UtilsFtp {

	private static final class CopyStreamListenerImpl implements CopyStreamListener {
		private long megsTotal = 0;

		public void bytesTransferred(CopyStreamEvent event) {
			bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
		}

		public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
			long megs = totalBytesTransferred / 1000000;
			for (long l = megsTotal; l < megs; l++) {
				System.err.print("#");
			}
			megsTotal = megs;
		}
	}

	public static FTPClient connect(String host, int port, String login, String password) throws BaseException {
		FTPClient client = new FTPClient();
		client.setCopyStreamListener(createListener());
		FTPClientConfig cfg = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		client.configure(cfg);

		try {
			try {
				client.connect(host);
			} catch (Exception ex) {
				throw new TechnicalException("Impossible to estabilish the connection to remote ftpHost: " + host, ex);
			}

			try {
				if (!client.login(login, password)) {
					throw new TechnicalException(
							"Impossible to estabilish the connection to remote ftpHost: " + host + ". Bad ftpLogin or ftpPassword");
				}
			} catch (IOException ex) {
				throw new TechnicalException("Impossible de se connecter vers le remote ftpHost: " + host, ex);
			}
			return client;
		} catch (BaseException ex) {
			try {
				client.disconnect();
			} catch (IOException ex2) {
				// nothing to do
			} finally {
			}
			throw ex;
		}
	}

	private static CopyStreamListener createListener() {
		return new CopyStreamListenerImpl();
	}

}
