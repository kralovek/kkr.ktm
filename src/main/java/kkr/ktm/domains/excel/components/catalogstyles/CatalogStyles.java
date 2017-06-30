package kkr.ktm.domains.excel.components.catalogstyles;

import kkr.ktm.domains.excel.components.exceladapter.TStyle;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.common.errors.BaseException;

public interface CatalogStyles {

	boolean isOpened();

	void close();

	void open(TWorkbook tWorkbook) throws BaseException;

	TStyle createStyle(String styleName) throws BaseException;

	TStyle updateStyle(String styleName, TStyle tStyleFrom) throws BaseException;
}
