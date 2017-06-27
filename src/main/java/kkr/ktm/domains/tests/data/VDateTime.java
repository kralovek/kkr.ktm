package kkr.ktm.domains.tests.data;

public class VDateTime extends VDate {

	private VTime time;

	public VDateTime(int year, int month, int day) {
		this(year, month, day, 0, 0, 0, 0);
	}

	public VDateTime(int year, int month, int day, int hour, int minute, int second) {
		this(year, month, day, hour, minute, second, 0);
	}

	public VDateTime(int year, int month, int day, int hour, int minute, int second, int milisecond) {
		super(year, month, day);
		time = new VTime(hour, minute, second, milisecond);
	}

	public int getHour() {
		return time.getHour();
	}

	public int getMinute() {
		return time.getMinute();
	}

	public int getSecond() {
		return time.getSecond();
	}

	public int getMilisecond() {
		return time.getMilisecond();
	}

	public int compareTo(VDate vDate) {
		int result = super.compareTo(vDate);
		if (result != 0) {
			return result;
		}

		if (vDate instanceof VDateTime) {
			VDateTime vDateTime = (VDateTime) vDate;
			return time.compareTo(vDateTime.time);
		}
		return 0;
	}
}
