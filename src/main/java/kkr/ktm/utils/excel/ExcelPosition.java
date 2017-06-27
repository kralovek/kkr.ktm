package kkr.ktm.utils.excel;

import java.io.File;

/**
 * ExcelPosition
 *
 * @author KRALOVEC-99999
 */
public class ExcelPosition implements Cloneable {
    private File file;

    private String sheet;

    private Integer column;

    private Integer row;

    public File getFile() {
        return file;
    }

    public void setFile(File pFile) {
        this.file = pFile;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String pSheet) {
        this.sheet = pSheet;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer pColumn) {
        this.column = pColumn;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(final Integer pRow) {
        this.row = pRow;
    }

    public String toString() {
        return "[(" // 
                + (file != null ? file.getAbsoluteFile() : "") //
                + ") " //
                + "{" + (sheet != null ? sheet : "") + "}" //
                + " " //
                + toCellId(column, row)
                + "]";
    }

    private static String toCellId(final Integer pColumn, final Integer pRow) {
        String column;
        String row;
        if (pColumn != null) {
            if (pColumn <= 'Z' - 'A') {
                column = String.valueOf((char) ((int) 'A' + pColumn));
            } else {
            	int range = ('Z' - 'A') + 1;
            	int A = (int) 'A';
                int c1 = (pColumn) / range - 1;
                int c2 = (pColumn - (c1 + 1) * range);
                column = String.valueOf((char) (A + c1)) + String.valueOf((char) (A + c2));
            }
        } else {
            column = "?";
        }
        if (pRow != null) {
            row = String.valueOf(pRow + 1);
        } else {
            row = "?";
        }
        return column + row;
    }
    
    public ExcelPosition clone() {
    	ExcelPosition ep = new ExcelPosition();
    	ep.file = file;
    	ep.row = row;
    	ep.sheet = sheet;
    	ep.column = column;
    	return ep;
    }
    
    public static final void main(String[] argv) {
    	for (int i = 0; i < 100; i++) {
        	System.out.println((i + 0) + " " + toCellId(i, 0));
    	}
    }
}
