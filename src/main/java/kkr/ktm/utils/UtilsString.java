package kkr.ktm.utils;

import java.util.Date;

public class UtilsString {
	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static String toStringDateDelta(Date dateBegin, Date dateEnd) {
		long delta = dateEnd.getTime() - dateBegin.getTime();

		long ms = delta % 1000;
		delta = (delta - ms) / 1000;

		long sec = delta % 60;
		delta = (delta - sec) / 60;

		if (delta == 0) {
			return String.format("%d.%03d", sec, ms);
		}

		long min = delta % 60;
		delta = (delta - min) / 60;

		if (delta == 0) {
			return String.format("%d:%02d.%03d", min, sec, ms);
		}

		long hour = delta % 24;
		delta = (delta - hour) / 24;

		if (delta == 0) {
			return String.format("%d:%02d:%02d.%03d", hour, min, sec, ms);
		}

		long day = delta;
		return String.format("%d days %d:%02d:%02d.%03d", day, hour, min, sec, ms);
	}
}
