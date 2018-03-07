package kkr.ktm.domains.common.components.parametersformater.template.format;

public interface Format {

	String format(Object value) throws IllegalArgumentException;

	FormatType getType();
}
