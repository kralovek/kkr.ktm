package kkr.ktm.domains.excelpoi.style;

import org.apache.poi.ss.usermodel.IndexedColors;

public interface Style {

	String getName();

	Short getPoiBoldweight();

	Short getPoiAlignment();

	IndexedColors getPoiBackgroundColor();

	IndexedColors getPoiForegroundColor();
}
