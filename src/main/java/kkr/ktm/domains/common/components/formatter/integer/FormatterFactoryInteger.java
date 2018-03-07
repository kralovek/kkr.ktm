package kkr.ktm.domains.common.components.formatter.integer;

import org.apache.log4j.Logger;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.FormatterFactory;

public class FormatterFactoryInteger extends FormatterFactoryIntegerFwk implements FormatterFactory {
	private static final Logger LOG = Logger.getLogger(FormatterFactoryInteger.class);

	public FormatterInteger createFormatter(String pattern) throws ConfigurationException {
		LOG.trace("BEGIN");
		try {
			FormatterInteger formatter = new FormatterInteger();
			formatter.setPattern(pattern);
			formatter.config();
			LOG.trace("OK");
			return formatter;
		} finally {
			LOG.trace("END");
		}
	}
}
