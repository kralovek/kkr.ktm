package kkr.ktm.components.runner;

import java.util.Map;

import kkr.ktm.exception.BaseException;


public interface Runner {
	Map<String, Object> run(Map<String, Object> parameters) throws BaseException;
}
