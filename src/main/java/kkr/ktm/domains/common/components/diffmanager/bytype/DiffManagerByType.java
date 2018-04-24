package kkr.ktm.domains.common.components.diffmanager.bytype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;

public class DiffManagerByType extends DiffManagerByTypeFwk {
	private static final Logger LOG = Logger.getLogger(DiffManagerByType.class);

	public Collection<DiffEntity> loadDiffs(String type, Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffManager> diffManagers = findDiffManagers(type);
			Collection<DiffEntity> retval = new ArrayList<DiffEntity>();
			for (DiffManager diffManager : diffManagers) {
				Collection<DiffEntity> diffEntities = diffManager.loadDiffs(groupStates);
				retval.addAll(diffEntities);
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public Collection<DiffEntity> loadCurrents(String type) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<DiffManager> diffManagers = findDiffManagers(type);
			Collection<DiffEntity> retval = new ArrayList<DiffEntity>();
			for (DiffManager diffManager : diffManagers) {
				Collection<DiffEntity> diffEntities = diffManager.loadCurrents();
				retval.addAll(diffEntities);
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Collection<DiffManager> findDiffManagers(String type) {
		Collection<DiffManager> retval = new ArrayList<DiffManager>();
		for (Map.Entry<Pattern, DiffManager> entry : diffManagers.entrySet()) {
			if (entry.getKey().matcher(type).matches()) {
				retval.add(entry.getValue());
			}
		}
		return retval;
	}
}
