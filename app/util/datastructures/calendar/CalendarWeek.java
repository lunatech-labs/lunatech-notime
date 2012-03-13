package util.datastructures.calendar;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import util.DateTimeUtil;

/*
 *  Datastructure for displaying a calendar
 */
public class CalendarWeek {

	public List<CalendarDay> days;

	public CalendarWeek() {
		days = new ArrayList<CalendarDay>();
	}

	public static List<CalendarWeek> createWeeks(DateTime firstDateOfMonth,	Long userId) {
		DateTime lastDateOfMonth = DateTimeUtil.lastDateOfMonth(firstDateOfMonth);
		DateTime firstDateOfWeek = DateTimeUtil.firstDateOfWeek(firstDateOfMonth);
		DateTime currentDate = firstDateOfWeek;

		List<CalendarWeek> weeks = new ArrayList<CalendarWeek>();
		
		while (currentDate.getMonthOfYear() <= lastDateOfMonth.getMonthOfYear()) {
			CalendarWeek week = new CalendarWeek();
			DateTime lastDateOfWeek = DateTimeUtil.lastDateOfWeek(currentDate);

			while (currentDate.getWeekOfWeekyear() == lastDateOfWeek
					.getWeekOfWeekyear()) {
				week.days.add(new CalendarDay(currentDate, userId));
				currentDate = currentDate.plusDays(1);
			}
			weeks.add(week);
			currentDate.plusDays(1);
		}
		return weeks;
	}

}
