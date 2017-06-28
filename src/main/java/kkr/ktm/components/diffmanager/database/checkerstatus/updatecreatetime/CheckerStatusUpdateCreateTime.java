package kkr.ktm.components.diffmanager.database.checkerstatus.updatecreatetime;

import java.util.Date;
import java.util.Map;

import kkr.ktm.components.diffmanager.data.DiffStatus;
import kkr.ktm.components.diffmanager.database.ItemCruid;
import kkr.ktm.components.diffmanager.database.checkerstatus.CheckerStatus;
import kkr.ktm.exception.BaseException;

public class CheckerStatusUpdateCreateTime extends CheckerStatusUpdateCreateTimeFwk implements CheckerStatus {
	public DiffStatus checkStatus(long index, ItemCruid itemCruid) throws BaseException {
		if (itemCruid == null || itemCruid.getParameters() == null || itemCruid.getParameters().isEmpty()) {
			return DiffStatus.NEW;
		}

		Object valueCreate = null;
		Object valueUpdate = null;

		for (Map.Entry<String, Object> entry : itemCruid.getParameters().entrySet()) {
			if (this.columnCreate.equals(entry.getKey())) {
				valueCreate = entry.getValue();
			}
			if (this.columnUpdate.equals(entry.getKey())) {
				valueUpdate = entry.getValue();
			}
		}

		if (valueCreate == null || valueUpdate == null) {
			return DiffStatus.NEW;
		}

		if (valueCreate.equals(valueUpdate)) {
			return DiffStatus.NEW;
		}

		Date dateTs = new Date(index);
		Date dateCreate = null;

		if (valueCreate instanceof Date) {
			dateCreate = (Date) valueCreate;
		}

		if (dateTs.compareTo(dateCreate) > 0) {
			return DiffStatus.UPD;
		}

		return DiffStatus.NEW;
	}
}
