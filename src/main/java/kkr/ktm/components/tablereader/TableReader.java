package kkr.ktm.components.tablereader;

import java.util.Collection;
import java.util.Map;

import kkr.ktm.exception.BaseException;

public interface TableReader {

	Collection<Map<String, Object>> readData(String source) throws BaseException;

}
