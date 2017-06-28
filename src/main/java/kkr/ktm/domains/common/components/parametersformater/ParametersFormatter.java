package kkr.ktm.domains.common.components.parametersformater;

import java.util.Map;

import kkr.ktm.exception.BaseException;

public interface ParametersFormatter {

	String format(String source, Map<String, Object> parameters) throws BaseException;
}
