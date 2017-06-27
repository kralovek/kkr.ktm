package kkr.ktm.domains.excelpoi.style;

import org.apache.poi.ss.usermodel.Font;

public enum Boldweight {
	NORMAL(Font.BOLDWEIGHT_NORMAL),
	BOLD(Font.BOLDWEIGHT_BOLD);

	private short value;

	private Boldweight(short value) {
		this.value = value;
	}

	public short getValue() {
		return value;
	}
}
