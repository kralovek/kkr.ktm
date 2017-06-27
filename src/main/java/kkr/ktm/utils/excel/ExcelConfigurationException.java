package kkr.ktm.utils.excel;

import kkr.ktm.exception.ConfigurationException;

/**
 * ExcelConfigurationException
 *
 * @author KRALOVEC-99999
 */
public class ExcelConfigurationException extends ConfigurationException {
    private static final long serialVersionUID = -2222237289970246694L;

    private ExcelPosition position;

    public ExcelConfigurationException(ExcelPosition excelPosition, String message) {
        super(message);
        this.position = excelPosition;
    }

    public ExcelConfigurationException(ExcelPosition excelPosition, String message, Throwable cause) {
        super(message, cause);
        this.position = excelPosition;
    }

    public ExcelPosition getPosition() {
        return position;
    }

    public String getMessage() {
        return
                (position != null ? position.toString() : "[?] ") //
                        + " - " + (super.getMessage() != null ? super.getMessage() : "");
    }
}
