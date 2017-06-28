package kkr.ktm.components.diffmanager;

import java.util.List;

import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.exception.BaseException;

public interface DiffManager {

	String getName();

	List<DiffGroup> loadDiffs(List<DiffGroup> groupStates) throws BaseException;

	List<DiffGroup> loadCurrents() throws BaseException;
}
