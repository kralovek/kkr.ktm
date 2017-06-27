package kkr.ktm.components.diffmanager.databasetrigger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kkr.ktm.components.diffmanager.DiffManager.Index;

public class IndexImpl implements Index, Comparable<Index> {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS");

	private Timestamp timestamp;

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int compareTo(Index index) {
		if (index instanceof IndexImpl) {
			IndexImpl indexImpl = (IndexImpl) index;
			if (timestamp != null && indexImpl.getTimestamp() != null) {
				return timestamp.compareTo(indexImpl.getTimestamp());
			} else if (timestamp == null && indexImpl.getTimestamp() == null) {
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
