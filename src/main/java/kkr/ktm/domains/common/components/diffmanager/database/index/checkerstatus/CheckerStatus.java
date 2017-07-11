package kkr.ktm.domains.common.components.diffmanager.database.index.checkerstatus;

import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;
import kkr.ktm.domains.common.components.diffmanager.database.data.ItemCruid;
import kkr.common.errors.BaseException;

public interface CheckerStatus {

	DiffStatus checkStatus(long index, ItemCruid itemCruid) throws BaseException;
}
