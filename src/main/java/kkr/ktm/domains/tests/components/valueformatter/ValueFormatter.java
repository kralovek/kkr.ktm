package kkr.ktm.domains.tests.components.valueformatter;

public interface ValueFormatter {

	String formatValue(Object object) throws ValueFormatException;
}
