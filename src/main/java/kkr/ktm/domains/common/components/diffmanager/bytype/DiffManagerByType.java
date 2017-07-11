package kkr.ktm.domains.common.components.diffmanager.bytype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.tests.data.Test;

public class DiffManagerByType extends DiffManagerByTypeFwk implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerByType.class);

	public Collection<DiffEntity> loadDiffs(Test test, Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffManager> diffManagers = findDiffManagers(test);
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
			Collection<DiffManager> diffManagers = findDiffManagers(test);
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

	private Collection<DiffManager> findDiffManagers(Test test) {
		Collection<DiffManager> retval = new ArrayList<DiffManager>();
		for (Map.Entry<Pattern, DiffManager> entry : diffManagers.entrySet()) {
			if (entry.getKey().matcher(test.getType()).matches()) {
				retval.add(entry.getValue());
			}
		}
		return retval;
	}
}
