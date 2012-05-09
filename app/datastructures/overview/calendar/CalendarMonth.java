package datastructures.overview.calendar;

import java.util.List;

import models.User;

import org.joda.time.LocalDate;

import utils.DateUtil;

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
	public CalendarMonth(LocalDate date, User user) {
		weeks = CalendarWeek.createWeeks(DateUtil.firstDateOfMonth(date),
				user);
	}
}
