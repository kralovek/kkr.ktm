package kkr.ktm.components.diffmanager.database.checkerstatus;

import kkr.ktm.components.diffmanager.DiffManager;
import kkr.ktm.components.diffmanager.database.ItemCruid;
import kkr.ktm.exception.BaseException;

public interface CheckerStatus {

	DiffManager.Status checkStatus(long index, ItemCruid itemCruid) throws BaseException;
}
