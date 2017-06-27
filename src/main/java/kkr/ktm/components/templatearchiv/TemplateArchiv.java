package kkr.ktm.components.templatearchiv;

import kkr.ktm.exception.BaseException;

/**
 * TemplateArchiv
 *
 * @author KRALOVEC-99999
 */
public interface TemplateArchiv {

    public String loadTemplate(final String pCode) throws BaseException;
}
