package kkr.ktm.domains.common.components.parametersformater.template2.content;

import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentEvaluateException;

public interface Content {
	String evaluate(ContextContent context) throws ContentEvaluateException;
}
