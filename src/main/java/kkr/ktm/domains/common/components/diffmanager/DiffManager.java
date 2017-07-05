package kkr.ktm.domains.common.components.diffmanager;

import java.util.Collection;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.data.DiffGroup;

public interface DiffManager {

	String getName();

	Collection<DiffGroup> loadDiffs(Collection<DiffGroup> groupStates) throws BaseException;

	Collection<DiffGroup> loadCurrents() throws BaseException;
}
