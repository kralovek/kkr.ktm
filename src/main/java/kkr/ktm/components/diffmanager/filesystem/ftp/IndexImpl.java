package kkr.ktm.components.diffmanager.filesystem.ftp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import kkr.ktm.components.diffmanager.DiffManager.Index;

public class IndexImpl implements Index, Comparable<Index> {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS");

	private long ms;

	public long getMs() {
		return ms;
	}

	public void setMs(long ms) {
		this.ms = ms;
	}

	public int compareTo(Index index) {
		if (index instanceof IndexImpl) {
			IndexImpl indexImpl = (IndexImpl) index;
			return ms < indexImpl.ms ? -1 : ms > indexImpl.ms ? +1 : 0;
		} else {
			return -1;
		}
	}

	public String toString() {
		Date date = new Date(ms);
		return DATE_FORMAT.format(date);
	}
}
