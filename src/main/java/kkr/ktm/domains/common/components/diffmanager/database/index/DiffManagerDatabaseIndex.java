package kkr.ktm.domains.common.components.diffmanager.database.index;

import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;

public class DiffManagerDatabaseIndex extends DiffManagerDatabaseIndexFwk implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerDatabaseIndex.class);

	public Collection<DiffEntity> loadDiffs(Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
			return null;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<DiffEntity> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.trace("OK");
			return null;
		} finally {
			LOG.trace("END");
		}
	}
}
