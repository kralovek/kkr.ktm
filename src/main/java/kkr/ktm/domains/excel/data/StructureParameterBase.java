package kkr.ktm.domains.excel.data;

public abstract class StructureParameterBase {
	protected String name;

	public StructureParameterBase(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
