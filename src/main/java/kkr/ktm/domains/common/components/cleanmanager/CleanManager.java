package kkr.ktm.domains.common.components.cleanmanager;

import java.util.List;

import kkr.common.errors.BaseException;

public interface CleanManager {

	String getName();

	static interface Group {
		String getName();
		List<String> getPatterns();
	}
	
	void clean(List<Group> groups) throws BaseException;
}
