package kkr.ktm.domains.common.components.formatter.date;

import org.apache.log4j.Logger;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.FormatterFactory;

public class FormatterFactoryDate extends FormatterFactoryDateFwk implements FormatterFactory {
	private static final Logger LOG = Logger.getLogger(FormatterFactoryDate.class);

	public FormatterDate createFormatter(String pattern) throws ConfigurationException {
		LOG.trace("BEGIN");
		try {
			FormatterDate formatter = new FormatterDate();
			formatter.setPattern(pattern);
			formatter.config();
			LOG.trace("OK");
			return formatter;
		} finally {
			LOG.trace("END");
		}
	}
}
