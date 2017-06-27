package kkr.ktm.components.locker;

import kkr.ktm.exception.BaseException;

public interface Locker {

	void lock() throws BaseException;
	
	void unlock() throws BaseException;
}
