package kkr.ktm.domains.common.components.diffmanager;

import java.util.Collection;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.tests.data.Test;

public interface DiffManager {

	Collection<DiffEntity> loadDiffs(Test test, Collection<DiffEntity> groupStates) throws BaseException;

	Collection<DiffEntity> loadCurrents(Test test) throws BaseException;
}
