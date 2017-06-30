package kkr.ktm.domains.excel.components.catalogstyles.poi;

import org.apache.log4j.Logger;

import kkr.ktm.domains.excel.components.catalogstyles.CatalogStylesFactory;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.common.errors.BaseException;

public class CatalogStylesFactoryPoi extends CatalogStylesFactoryPoiFwk implements CatalogStylesFactory {
	private static final Logger LOG = Logger.getLogger(CatalogStylesFactoryPoi.class);

	public CatalogStylesPoi createInstance(TWorkbook tWorkbook) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			CatalogStylesPoi instance = new CatalogStylesPoi();
			instance.setStyles(getStyles());
			instance.config();
			instance.open(tWorkbook);

			LOG.trace("OK");
			return instance;
		} finally {
			LOG.trace("END");
		}
	}

}
