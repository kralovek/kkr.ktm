package kkr.ktm.domains.tests.components.valueparser;

import java.util.Collection;
import java.util.Date;

public interface ValueParser {

	boolean compareValue(Object valueO, Object valueE, Collection<Flag> flags);

	Number parseValueNumber(String value);

	Boolean parseValueBoolean(String value);

	Date parseValueDate(String value);

	Object parseValue(String value) throws ValueParseException;

	Object parseValueFlag(String value, Collection<Flag> outputParameters) throws ValueParseException;
}
