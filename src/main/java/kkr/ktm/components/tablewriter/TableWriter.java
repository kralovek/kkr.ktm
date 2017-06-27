package kkr.ktm.components.tablewriter;

import java.util.Collection;

import kkr.ktm.exception.BaseException;

public interface TableWriter {

	void writeData(String target, Collection<String> parameters, Collection<Collection<Object>> data) throws BaseException;
}
