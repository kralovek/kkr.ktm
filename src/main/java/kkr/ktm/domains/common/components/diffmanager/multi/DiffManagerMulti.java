package kkr.ktm.domains.common.components.diffmanager.multi;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.tests.data.Test;

public class DiffManagerMulti extends DiffManagerMultiFwk implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerMulti.class);

	public Collection<DiffEntity> loadDiffs(Test test, Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffEntity> retval = new ArrayList<DiffEntity>();
			for (DiffManager diffManager : diffManagers) {
				Collection<DiffEntity> diffEntities = diffManager.loadDiffs(test, groupStates);
				retval.addAll(diffEntities);
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<DiffEntity> loadCurrents(Test test) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffEntity> retval = new ArrayList<DiffEntity>();
			for (DiffManager diffManager : diffManagers) {
				Collection<DiffEntity> diffEntities = diffManager.loadCurrents(test);
				retval.addAll(diffEntities);
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
