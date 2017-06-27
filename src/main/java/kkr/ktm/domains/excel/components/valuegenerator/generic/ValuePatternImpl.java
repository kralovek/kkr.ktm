package kkr.ktm.domains.excel.components.valuegenerator.generic;

import java.util.Collection;
import java.util.HashSet;

import kkr.ktm.domains.tests.data.ValueFlag;
import kkr.ktm.domains.tests.data.ValuePattern;

public class ValuePatternImpl implements ValuePattern {
	private Object value;

	private Collection<ValueFlag> flags = new HashSet<ValueFlag>();

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Collection<ValueFlag> getFlags() {
		return flags;
	}
}
