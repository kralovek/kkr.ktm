package kkr.ktm.components.diffmanager.data;

import java.util.List;

public interface DiffGroup {
	String getName();
	DiffIndex getLastIndex();
	List<DiffItem> getItems();
}
