package kkr.ktm.components.diffmanager.empty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import kkr.ktm.components.diffmanager.DiffManager;
import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.exception.BaseException;

public class DiffManagerEmpty implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerEmpty.class);

	public String getName() {
		return "EMPTY";
	}

	public List<DiffGroup> loadDiffs(List<DiffGroup> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<DiffGroup> retval = new ArrayList<DiffGroup>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public List<DiffGroup> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<DiffGroup> retval = new ArrayList<DiffGroup>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
