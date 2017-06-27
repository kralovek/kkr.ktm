package kkr.ktm.components.diffmanager;

import java.util.List;
import java.util.Map;

import kkr.ktm.exception.BaseException;


public interface DiffManager {

	enum Status {
		NEW, UPD, DEL, UNK
	}
	
	interface Index {
	}
	
	interface Item {
		String getName();
		Index getIndex();
		Status getStatus();
		Map<String, String> getParameters();
	}

	interface Group {
		String getName();
		Index getLastIndex();
		List<Item> getItems();
	}

	String getName();
	
	List<Group> loadDiffs(List<Group> groupStates) throws BaseException;

	List<Group> loadCurrents() throws BaseException;
}
