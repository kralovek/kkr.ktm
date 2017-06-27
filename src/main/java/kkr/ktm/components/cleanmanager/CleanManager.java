package kkr.ktm.components.cleanmanager;

import java.util.List;

import kkr.ktm.exception.BaseException;

public interface CleanManager {

	String getName();

	static interface Group {
		String getName();
		List<String> getPatterns();
	}
	
	void clean(List<Group> groups) throws BaseException;
}
