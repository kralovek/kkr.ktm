package kkr.ktm.components.diffmanager.data;

import java.util.Map;

public interface DiffItem {
	String getName();
	DiffIndex getIndex();
	DiffStatus getStatus();
	Map<String, String> getParameters();
}