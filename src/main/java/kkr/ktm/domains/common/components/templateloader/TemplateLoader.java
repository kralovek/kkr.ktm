package kkr.ktm.domains.common.components.templateloader;

import kkr.common.errors.BaseException;

public interface TemplateLoader {

	public String loadTemplate(String name) throws BaseException;
}
