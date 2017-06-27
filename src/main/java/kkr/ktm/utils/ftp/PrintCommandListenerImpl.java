package kkr.ktm.utils.ftp;

import java.io.PrintWriter;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.log4j.Logger;



public class PrintCommandListenerImpl implements ProtocolCommandListener {
	private static final Logger LOG = Logger
	.getLogger(PrintCommandListenerImpl.class);

	private PrintWriter __writer;

	public PrintCommandListenerImpl(PrintWriter writer) {
		__writer = writer;
	}

	public void protocolCommandSent(ProtocolCommandEvent event) {
		LOG.trace("BEGIN");
		try {
			__writer.print(event.getMessage());
			__writer.flush();
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void protocolReplyReceived(ProtocolCommandEvent event) {
		LOG.trace("BEGIN");
		try {
			__writer.print(event.getMessage());
			__writer.flush();
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
