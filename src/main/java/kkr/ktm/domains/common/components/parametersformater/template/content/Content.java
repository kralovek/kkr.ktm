package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;

public interface Content {
	String evaluate(Context context) throws ContentEvaluateException;

	void validate(Context context) throws ContentParseException;
}
