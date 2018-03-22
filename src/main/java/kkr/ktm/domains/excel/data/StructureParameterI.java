package kkr.ktm.domains.excel.data;

public class StructureParameterI extends StructureParameterBase {

	private int index;

	public StructureParameterI(String name, int index) {
		super(name);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
