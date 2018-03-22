package kkr.ktm.domains.excel.data;

public class StructureParameterE extends StructureParameterBase {
	private int index;

	public StructureParameterE(String name, int index) {
		super(name);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
