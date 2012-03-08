package util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

public class DateUtil {

	public static DateTime maximizeTimeOfDate(DateTime date) {
		return new DateTime(date.getYear(), date.getMonthOfYear(),
				date.getDayOfMonth(), 23, 59, 59, 999);
	}

	public static boolean between(DateTime dateToCheck, DateTime beginDate,
			DateTime endDate) {
		Interval interval = new Interval(beginDate, endDate);
		return interval.contains(dateToCheck);
	}

	public static int currentWeekNumber() {
		return new DateTime().getWeekOfWeekyear();
	}

	public static DateTime firstDateOfWeek(DateTime date) {
		LocalDate now = date.toLocalDate();
		return now.withDayOfWeek(DateTimeConstants.MONDAY)
				.toDateTimeAtStartOfDay();
	}

	public static DateTime lastDateOfWeek(DateTime date) {
		LocalDate now = date.toLocalDate();
		return now.withDayOfWeek(DateTimeConstants.SUNDAY)
				.toDateTimeAtStartOfDay();
	}

}
