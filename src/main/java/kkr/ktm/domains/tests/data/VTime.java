package kkr.ktm.domains.tests.data;

public class VTime implements Comparable<VTime> {

	private int hour;
	private int minute;
	private int second;
	private int milisecond;

	public VTime(int hour, int minute, int second) {
		this(hour, minute, second, 0);
	}

	public VTime(int hour, int minute, int second, int milisecond) {
		if (hour < 0 || hour >= 24) {
			throw new IllegalArgumentException("Bad value of hour: " + hour);
		}
		this.hour = hour;

		if (minute < 0 || minute >= 60) {
			throw new IllegalArgumentException("Bad value of minute: " + minute);
		}
		this.minute = minute;

		if (second < 0 || second >= 60) {
			throw new IllegalArgumentException("Bad value of second: " + second);
		}
		this.second = second;

		if (milisecond < 0 || milisecond >= 1000) {
			throw new IllegalArgumentException("Bad value of milisecond: " + milisecond);
		}
		this.milisecond = milisecond;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public int getMilisecond() {
		return milisecond;
	}

	public int compareTo(VTime vTime) {
		return //
		hour < vTime.hour //
				? -1
				: hour > vTime.hour //
						? +1 //
						: minute < vTime.minute //
								? -1
								: minute > vTime.minute //
										? +1 //
										: second < vTime.second //
												? -1
												: second > vTime.second //
														? +1 //
														: milisecond < vTime.milisecond ? -1 : milisecond > vTime.milisecond
																? +1 //
																: 0;
	}
}
