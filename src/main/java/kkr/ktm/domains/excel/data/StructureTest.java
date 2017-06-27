package kkr.ktm.domains.excel.data;

public class StructureTest {

	private String code;
	private String name;
	private String description;

	private Integer order;
	private Integer group;
	private int index;
	private Status status;

	public StructureTest(int index) {
		super();
		this.index = index;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getIndex() {
		return index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
