package kkr.ktm.components.diffmanager.empty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import kkr.ktm.components.diffmanager.DiffManager;
import kkr.ktm.exception.BaseException;


public class DiffManagerEmpty implements DiffManager {
	private static final Logger LOG = Logger
			.getLogger(DiffManagerEmpty.class);

	public String getName() {
		return "EMPTY";
	}

	public List<Group> loadDiffs(List<Group> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<Group> retval = new ArrayList<DiffManager.Group>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public List<Group> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<Group> retval = new ArrayList<DiffManager.Group>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
