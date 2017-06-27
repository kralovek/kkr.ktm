package kkr.ktm.domains.excelpoi.style;

import org.apache.poi.ss.usermodel.CellStyle;

public enum Alignment {
	GENERAL(CellStyle.ALIGN_GENERAL),
	LEFT(CellStyle.ALIGN_LEFT),
	CENTER(CellStyle.ALIGN_CENTER),
	RIGHT(CellStyle.ALIGN_RIGHT),
	FILL(CellStyle.ALIGN_FILL),
	JUSTIFY(CellStyle.ALIGN_JUSTIFY),
	CENTER_SELECTION(CellStyle.ALIGN_CENTER_SELECTION);

	private short value;

	private Alignment(short value) {
		this.value = value;
	}

	public short getValue() {
		return value;
	}
}
