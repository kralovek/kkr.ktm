package kkr.ktm.domains.common.components.formatter.string;

import org.apache.log4j.Logger;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.FormatterFactory;

public class FormatterFactoryString extends FormatterFactoryStringFwk implements FormatterFactory {
	private static final Logger LOG = Logger.getLogger(FormatterFactoryString.class);

	public FormatterString createFormatter(String pattern) throws ConfigurationException {
		LOG.trace("BEGIN");
		try {
			FormatterString formatter = new FormatterString();
			formatter.setPattern(pattern);
			formatter.config();
			LOG.trace("OK");
			return formatter;
		} finally {
			LOG.trace("END");
		}
	}
}
