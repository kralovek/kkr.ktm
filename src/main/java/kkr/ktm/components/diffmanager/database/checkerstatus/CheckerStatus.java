package kkr.ktm.components.diffmanager.database.checkerstatus;

import kkr.ktm.components.diffmanager.data.DiffStatus;
import kkr.ktm.components.diffmanager.database.ItemCruid;
import kkr.ktm.exception.BaseException;

public interface CheckerStatus {

	DiffStatus checkStatus(long index, ItemCruid itemCruid) throws BaseException;
}
