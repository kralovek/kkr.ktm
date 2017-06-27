package kkr.ktm.components.locker.empty;

import org.apache.log4j.Logger;

import kkr.ktm.components.locker.Locker;
import kkr.ktm.exception.BaseException;


public class LockerEmpty implements Locker {
	private static final Logger LOG = Logger.getLogger(LockerEmpty.class);

	public void lock() throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.debug("LOCK IS NOT IMPLEMENTED");
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void unlock() throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.debug("LOCK IS NOT IMPLEMENTED");
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	
}
