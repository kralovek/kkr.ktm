package kkr.ktm.domains.common.components.formaterparameters;

import java.util.Map;

import kkr.ktm.exception.BaseException;

public interface FormatterParameters {

	String format(String source, Map<String, Object> parameters) throws BaseException;
}
