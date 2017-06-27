package kkr.ktm.domains.tests.data;

public class VDate implements Comparable<VDate> {

	private int year;
	private int month;
	private int day;

	public VDate(int year, int month, int day) {
		if (year < 1) {
			throw new IllegalArgumentException("Bad value of year: " + year);
		}
		this.year = year;

		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Bad value of month: " + month);
		}
		this.month = month;

		int maxDay;
		switch (month) {
			case 4 :
			case 6 :
			case 9 :
			case 11 :
				maxDay = 30;
				break;

			case 2 :
				maxDay = // 
						year % 500 == 0 // 
								? 29 //
								: year % 100 == 0 // 
										? 28 //
										: year % 4 == 0 // 
												? 29 //
												: 28;
				break;
			default :
				maxDay = 31;
		}

		if (day < 1 || day > maxDay) {
			throw new IllegalArgumentException("Bad value of days of month " + month + ": " + day);
		}
		this.day = day;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int compareTo(VDate vDate) {
		return //
		year < vDate.year //
				? -1
				: year > vDate.year //
						? +1 //
						: month < vDate.month //
								? -1
								: month > vDate.month //
										? +1 //
										: day < vDate.day //
												? -1
												: day > vDate.day //
														? +1 //
														: 0;
	}
}
