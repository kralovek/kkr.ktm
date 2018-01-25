package kkr.ktm.domains.common.components.parametersformater.template.format;

import kkr.ktm.domains.common.components.parametersformater.template.value.Value;

public interface Format {

	String format(Value value);

	FormatType getType();
}
