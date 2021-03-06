package kkr.ktm.domains.common.components.diffmanager.empty;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;

public class DiffManagerEmpty implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerEmpty.class);

	public String getCode() {
		return "EMPTY";
	}

	public Collection<DiffEntity> loadDiffs(Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffEntity> retval = new ArrayList<DiffEntity>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<DiffEntity> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffEntity> retval = new ArrayList<DiffEntity>();
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
