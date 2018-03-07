package kkr.ktm.domains.common.components.context.name;

import java.util.HashSet;
import java.util.Set;

import kkr.ktm.domains.common.components.context.Context;

public class ContextName implements Context {

	private Set<String> names = new HashSet<String>();

	public boolean isName(String name) {
		return names.contains(name);
	}

	public void addName(String name) {
		names.add(name);
	}

	public void removeName(String name) {
		names.remove(name);
	}
}
