package kkr.ktm.components.templateparser.impl;

import kkr.ktm.exception.ConfigurationException;

/**
 * TemplateConfigurationException
 *
 * @author KRALOVEC-99999
 */
public class TemplateConfigurationException extends ConfigurationException {
    private static final long serialVersionUID = -3332237289970246694L;

    private TemplatePosition position;

    public TemplateConfigurationException(final TemplatePosition pPosition, final String pMsg) {
        super(pMsg);
    }

    public TemplateConfigurationException(final TemplatePosition pPosition, final String pMsg, final Throwable pCause) {
        super(pMsg, pCause);
    }

    public TemplatePosition getPosition() {
        return position;
    }

    public String getMessage() {
        return (position != null ? position.toString() : "") + (super.getMessage() != null ? super.getMessage() : "");
    }
}
