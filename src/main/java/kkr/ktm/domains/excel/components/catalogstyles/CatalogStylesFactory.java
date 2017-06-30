package kkr.ktm.domains.excel.components.catalogstyles;

import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.common.errors.BaseException;

public interface CatalogStylesFactory {

	CatalogStyles createInstance(TWorkbook tWorkbook) throws BaseException;
}
