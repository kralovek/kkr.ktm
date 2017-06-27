package kkr.ktm.utils.ftp;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.log4j.Logger;



public class ProtocolCommandListenerImpl implements ProtocolCommandListener {
	private static final Logger LOG = Logger
	.getLogger(ProtocolCommandListenerImpl.class);
	
	public void protocolCommandSent(ProtocolCommandEvent protocolcommandevent) {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void protocolReplyReceived(ProtocolCommandEvent protocolcommandevent) {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
