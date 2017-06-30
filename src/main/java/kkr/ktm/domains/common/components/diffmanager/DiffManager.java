package kkr.ktm.domains.common.components.diffmanager;

import java.util.List;

import kkr.ktm.domains.common.components.diffmanager.data.DiffGroup;
import kkr.common.errors.BaseException;

public interface DiffManager {

	String getName();

	List<DiffGroup> loadDiffs(List<DiffGroup> groupStates) throws BaseException;

	List<DiffGroup> loadCurrents() throws BaseException;
}
