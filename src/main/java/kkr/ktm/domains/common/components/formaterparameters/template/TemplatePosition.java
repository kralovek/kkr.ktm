package kkr.ktm.domains.common.components.formaterparameters.template;

import java.io.File;

/**
 * TemplatePosition
 *
 * @author KRALOVEC-99999
 */
public class TemplatePosition {
    private File file;
    private Integer position;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String toString() {
        return "[" // 
                + (file != null ? file.getAbsoluteFile() : "") //
                + " " //
                + (position != null ? position : "") //
                + "]";
    }
}
