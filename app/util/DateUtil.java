package util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class DateUtil {
	
	public static Date maximizeTimeOfDate(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
	
	public static boolean between(Date dateToCheck, Date beginDate, Date endDate) {		
		return dateToCheck.after(beginDate) && dateToCheck.before(maximizeTimeOfDate(endDate));
	}
	
	public static int currentWeekNumber() {
		return new DateTime().getWeekOfWeekyear();
	}
	
	public static DateTime firstDateOfWeek(DateTime date) {
		LocalDate now = date.toLocalDate();
		return now.withDayOfWeek(DateTimeConstants.MONDAY).toDateTimeAtStartOfDay();
	}
	
	public static DateTime lastDateOfWeek(DateTime date) {
		LocalDate now = date.toLocalDate();
		return now.withDayOfWeek(DateTimeConstants.SUNDAY).toDateTimeAtStartOfDay();
	}

}
