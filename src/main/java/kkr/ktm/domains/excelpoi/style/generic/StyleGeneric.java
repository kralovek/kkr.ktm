package kkr.ktm.domains.excelpoi.style.generic;

import org.apache.poi.ss.usermodel.IndexedColors;

import kkr.ktm.domains.excelpoi.style.Style;

public class StyleGeneric extends StyleGenericFwk implements Style {

	public Short getPoiBoldweight() {
		return poiBoldweight;
	}

	public Short getPoiAlignment() {
		return poiAlignment;
	}

	public IndexedColors getPoiBackgroundColor() {
		return poiBackgroundColor;
	}

	public IndexedColors getPoiForegroundColor() {
		return poiForegroundColor;
	}

	public String toString() {
		return name;
	}
}
