package kkr.ktm.domains.common.components.diffmanager.databasetrigger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kkr.ktm.domains.common.components.diffmanager.data.DiffIndex;

public class DiffIndexImpl implements DiffIndex, Comparable<DiffIndex> {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private Timestamp timestamp;

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int compareTo(DiffIndex diffIndex) {
		if (diffIndex instanceof DiffIndexImpl) {
			DiffIndexImpl diffIndexImpl = (DiffIndexImpl) diffIndex;
			if (timestamp != null && diffIndexImpl.getTimestamp() != null) {
				return timestamp.compareTo(diffIndexImpl.getTimestamp());
			} else if (timestamp == null && diffIndexImpl.getTimestamp() == null) {
				return 0;
			} else {
				return timestamp == null ? -1 : +1;
			}
		} else {
			return -1;
		}
	}

	public String toString() {
		if (timestamp != null) {
			return DATE_FORMAT.format(timestamp);
		} else {
			return DATE_FORMAT.format(new Timestamp(0L));
		}
	}
}
