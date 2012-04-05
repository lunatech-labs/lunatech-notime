package datastructures.overview.calendar;

import java.util.List;

import org.joda.time.DateTime;

import util.DateTimeUtil;

/**
 * Datastructure for displaying a calendar 
 */
public class CalendarMonth {

	public final List<CalendarWeek> weeks;

	/**
	 * Creates the month, with weeks, with days
	 * 
	 * @param date
	 *            A certain date in the month for which the data structure is
	 *            created
	 */
	public CalendarMonth(DateTime date, Long userId) {
		weeks = CalendarWeek.createWeeks(DateTimeUtil.firstDateOfMonth(date),
				userId);
	}
}
