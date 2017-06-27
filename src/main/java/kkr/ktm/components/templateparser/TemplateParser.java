package kkr.ktm.components.templateparser;

import java.util.Map;

import kkr.ktm.exception.BaseException;

public interface TemplateParser {
	String parse(String pSource, Map<String, Object> pParameters) throws BaseException;
}
