package kkr.ktm.domains.common.components.formatter.bytype;

import org.apache.log4j.Logger;

import kkr.common.errors.ConfigurationException;

public class FormatterFactoryByType extends FormatterFactoryByTypeFwk {
	private static final Logger LOG = Logger.getLogger(FormatterFactoryByType.class);

	public FormatterByType createFormatter( //
			String patternString, //
			String patternBoolean, //
			String patternInteger, //
			String patternDecimal, //
			String patternDate //
	) throws ConfigurationException {
		LOG.trace("BEGIN");
		try {
			FormatterByType formatter = new FormatterByType();
			formatter.setFormatterAuto(formatterFactoryAuto.createFormatter(null));
			if (patternString != null) {
				formatter.setFormatterString(formatterFactoryString.createFormatter(patternString));
			}
			if (patternBoolean != null) {
				formatter.setFormatterBoolean(formatterFactoryBoolean.createFormatter(patternBoolean));
			}
			if (patternInteger != null) {
				formatter.setFormatterInteger(formatterFactoryInteger.createFormatter(patternInteger));
			}
			if (patternDecimal != null) {
				formatter.setFormatterDecimal(formatterFactoryDecimal.createFormatter(patternDecimal));
			}
			if (patternDate != null) {
				formatter.setFormatterDate(formatterFactoryDate.createFormatter(patternDate));
			}

			formatter.config();
			LOG.trace("OK");
			return formatter;
		} finally {
			LOG.trace("END");
		}
	}
}
