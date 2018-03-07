package kkr.ktm.domains.common.components.formatter.decimal;

import org.apache.log4j.Logger;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.FormatterFactory;

public class FormatterFactoryDecimal extends FormatterFactoryDecimalFwk implements FormatterFactory {
	private static final Logger LOG = Logger.getLogger(FormatterFactoryDecimal.class);

	public FormatterDecimal createFormatter(String pattern) throws ConfigurationException {
		LOG.trace("BEGIN");
		try {
			FormatterDecimal formatter = new FormatterDecimal();
			formatter.setPattern(pattern);
			formatter.config();
			LOG.trace("OK");
			return formatter;
		} finally {
			LOG.trace("END");
		}
	}
}
