package kkr.ktm.domains.excel.components.exceladapter.poi;

import kkr.ktm.domains.excel.components.exceladapter.TColor;

public class TColorPoi implements TColor {

	private short color;
	
	public TColorPoi(short color) {
		this.color = color;
	}
	
	public short getColor() {
		return color;
	}
}
