package kkr.ktm.domains.common.components.diffmanager.empty;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffGroup;

public class DiffManagerEmpty implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerEmpty.class);

	public String getName() {
		return "EMPTY";
	}

	public Collection<DiffGroup> loadDiffs(Collection<DiffGroup> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffGroup> retval = new ArrayList<DiffGroup>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<DiffGroup> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffGroup> retval = new ArrayList<DiffGroup>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
