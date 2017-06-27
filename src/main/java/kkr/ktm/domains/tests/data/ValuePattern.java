package kkr.ktm.domains.tests.data;

import java.util.Collection;

public interface ValuePattern {
	Object getValue();

	Collection<ValueFlag> getFlags();
}
