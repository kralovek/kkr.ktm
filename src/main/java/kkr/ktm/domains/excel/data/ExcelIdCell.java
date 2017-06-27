package kkr.ktm.domains.excel.data;

public class ExcelIdCell implements Comparable<ExcelIdCell> {
	private int row;
	private int column;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int hashCode() {
		return ("" + row + ":" + column).hashCode();
	}

	public boolean equals(Object object) {
		return object instanceof ExcelIdCell && row == ((ExcelIdCell) object).row && column == ((ExcelIdCell) object).column;
	}

	public int compareTo(ExcelIdCell excelIdCell) {
		return row < excelIdCell.row //
				? -1 //
				: row > excelIdCell.row //
						? +1 //
						: column < excelIdCell.column //
								? -1 //
								: column > excelIdCell.column //
										? +1 //
										: 0;
	}

	public String toString() {
		return "[" + row + ":" + column + "]";
	}
}
