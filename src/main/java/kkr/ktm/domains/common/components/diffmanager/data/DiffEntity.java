package kkr.ktm.domains.common.components.diffmanager.data;

import java.util.Collection;

public interface DiffEntity {
	String getName();
	DiffIndex getLastIndex();
	Collection<DiffItem> getItems();
}
