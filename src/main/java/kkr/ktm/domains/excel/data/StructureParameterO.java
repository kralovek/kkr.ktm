package kkr.ktm.domains.excel.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class StructureParameterO extends StructureParameterBase {

	private Collection<Integer> indexes = new TreeSet<Integer>();

	public StructureParameterO(String name) {
		super(name);
	}

	public Iterator<Integer> iteratorIndexes() {
		return indexes.iterator();
	}

	public void addIndex(int index) {
		indexes.add(index);
	}
}
