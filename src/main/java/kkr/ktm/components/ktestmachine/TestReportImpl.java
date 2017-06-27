package kkr.ktm.components.ktestmachine;

import java.util.Date;

import kkr.ktm.components.batchreporter.TestReport;

public class TestReportImpl implements TestReport {

	private String source;

	private String type;

	private String id;

	private String name;

	private String description;

	private Date timeBegin;

	private Date timeEnd;

	private Boolean ok;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getTimeBegin() {
		return timeBegin;
	}

	public void setTimeBegin(Date timeBegin) {
		this.timeBegin = timeBegin;
	}

	public Date getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	public Boolean getOk() {
		return ok;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}
}
