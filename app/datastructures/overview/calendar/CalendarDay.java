package datastructures.overview.calendar;

import java.util.Collections;
import java.util.List;

import models.HourEntry;

import org.joda.time.LocalDate;

/**
 * Datastructure for displaying a calendar
 */
public class CalendarDay {

	public final LocalDate date;

	public final Long userId;

	private List<HourEntry> hourEntries;

	/**
	 * Constructor for {@link CalendarDay}. Also tries to find all the entered
	 * hours for this day.
	 * 
	 * @param date
	 * @param userId
	 */
	public CalendarDay(LocalDate date, Long userId) {
		this.date = date;
		this.userId = userId;
	}

	public List<HourEntry> getHourEntries() {
		hourEntries = HourEntry.findAllForUserForDay(userId, date);
		return Collections.unmodifiableList(hourEntries);
	}

}
