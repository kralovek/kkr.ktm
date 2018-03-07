package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentEvaluateException;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;

public class ContentEmpty implements Content {
	public String evaluate(Context context) throws ContentEvaluateException {
		return "";
	}

	public void validate(Context context) throws ContentParseException {
	}
}
