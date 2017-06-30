package kkr.ktm.domains.excel.components.stylescatalogue;

import kkr.ktm.domains.excel.components.exceladapter.TStyle;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.common.errors.BaseException;

public interface StylesCatalogue {

	TStyle createStyle(TWorkbook genWorkbook, String styleName) throws BaseException;

	TStyle createStyle(TWorkbook genWorkbook, TStyle genStyleFrom, String styleName) throws BaseException;
}
