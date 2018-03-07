package kkr.ktm.domains.common.components.formatter.auto;

import org.apache.log4j.Logger;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.FormatterFactory;

public class FormatterFactoryAuto extends FormatterFactoryAutoFwk implements FormatterFactory {
	private static final Logger LOG = Logger.getLogger(FormatterFactoryAuto.class);

	public FormatterAuto createFormatter(String pattern) throws ConfigurationException {
		LOG.trace("BEGIN");
		try {
			FormatterAuto formatter = new FormatterAuto();
			formatter.config();
			LOG.trace("OK");
			return formatter;
		} finally {
			LOG.trace("END");
		}
	}

}
