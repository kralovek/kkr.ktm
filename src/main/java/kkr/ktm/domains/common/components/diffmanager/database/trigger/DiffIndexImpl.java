package kkr.ktm.domains.common.components.diffmanager.database.trigger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kkr.ktm.domains.common.components.diffmanager.data.DiffIndex;

public class DiffIndexImpl implements DiffIndex, Comparable<DiffIndex> {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private Timestamp timestamp;

	public DiffIndexImpl(Timestamp timestamp) {
		if (timestamp == null) {
			throw new IllegalArgumentException("timestamp is null");
		}
		this.timestamp = timestamp;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public int compareTo(DiffIndex diffIndex) {
		DiffIndexImpl diffIndexImpl = (DiffIndexImpl) diffIndex;
		return timestamp.compareTo(diffIndexImpl.getTimestamp());
	}

	public String toString() {
		return DATE_FORMAT.format(timestamp);
	}
}
