package kkr.ktm.domains.common.components.diffmanager;

import java.util.Collection;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;

public interface DiffManager {
	Collection<DiffEntity> loadDiffs(Collection<DiffEntity> groupStates) throws BaseException;

	Collection<DiffEntity> loadCurrents() throws BaseException;
}
