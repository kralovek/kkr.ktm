package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;

public interface Content {
	String evaluate(ContextContent context) throws ContentEvaluateException;
}
