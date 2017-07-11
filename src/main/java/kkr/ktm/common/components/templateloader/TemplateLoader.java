package kkr.ktm.common.components.templateloader;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.tests.data.Test;

public interface TemplateLoader {

	public String loadTemplate(Test test) throws BaseException;
}
