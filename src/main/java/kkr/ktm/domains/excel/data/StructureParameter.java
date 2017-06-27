package kkr.ktm.domains.excel.data;

public class StructureParameter {
	private Io io;
	private String name;
	private int index;

	public StructureParameter(int index) {
		super();
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public Io getIo() {
		return io;
	}
	public void setIo(Io io) {
		this.io = io;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
