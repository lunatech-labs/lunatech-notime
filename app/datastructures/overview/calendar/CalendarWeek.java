package datastructures.overview.calendar;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;

import utils.DateUtil;

/**
 * Datastructure for displaying a calendar
 */
public class CalendarWeek {

	public final List<CalendarDay> days;

	public CalendarWeek() {
		days = new LinkedList<CalendarDay>();
	}

	/**
	 * Creates the weeks. Only full weeks are created, so the beginning of the
	 * first week can contain a few days from the last month and the last week
	 * can contain a few days of the next month.
	 * 
	 * @param firstDateOfMonth
	 * @param userId
	 * @return
	 */
	public static List<CalendarWeek> createWeeks(LocalDate firstDateOfMonth,
			Long userId) {
		LocalDate lastDateOfMonth = DateUtil
				.lastDateOfMonth(firstDateOfMonth);
		LocalDate firstDateOfWeek = DateUtil
				.firstDateOfWeek(firstDateOfMonth);
		LocalDate currentDate = firstDateOfWeek;

		List<CalendarWeek> weeks = new LinkedList<CalendarWeek>();

		while (currentDate.getMonthOfYear() <= lastDateOfMonth.getMonthOfYear()) {
			CalendarWeek week = new CalendarWeek();
			LocalDate lastDateOfWeek = DateUtil.lastDateOfWeek(currentDate);

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
