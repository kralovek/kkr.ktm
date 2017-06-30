package kkr.ktm.domains.common.components.parametersparser;

import java.util.Map;

import kkr.common.errors.BaseException;

public interface ParametersParser {

	Map<String, Object> parse(String source) throws BaseException;

}
