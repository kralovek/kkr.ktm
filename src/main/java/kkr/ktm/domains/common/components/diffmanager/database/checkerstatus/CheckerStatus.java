package kkr.ktm.domains.common.components.diffmanager.database.checkerstatus;

import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;
import kkr.ktm.domains.common.components.diffmanager.database.ItemCruid;
import kkr.ktm.exception.BaseException;

public interface CheckerStatus {

	DiffStatus checkStatus(long index, ItemCruid itemCruid) throws BaseException;
}
