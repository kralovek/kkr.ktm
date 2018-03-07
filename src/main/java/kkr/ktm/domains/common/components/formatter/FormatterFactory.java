package kkr.ktm.domains.common.components.formatter;

import kkr.common.errors.ConfigurationException;

public interface FormatterFactory {
	Formatter createFormatter(String pattern) throws ConfigurationException;
}
