package kkr.ktm.components.batchreporter;

import java.util.Date;

public interface TestReport {
	String getSource();

	String getType();
	
	String getId();

	String getName();

	String getDescription();

	Date getTimeBegin();

	Date getTimeEnd();

	Boolean getOk();
}