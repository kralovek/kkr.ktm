package kkr.ktm.domains.common.components.parserparameters;

import java.util.Map;

import kkr.ktm.exception.BaseException;

public interface ParserParameters {

	Map<String, Object> parse(String source) throws BaseException;

}
