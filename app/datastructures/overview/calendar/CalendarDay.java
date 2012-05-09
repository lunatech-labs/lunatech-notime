package datastructures.overview.calendar;

import java.util.Collections;
import java.util.List;

import models.HourEntry;
import models.User;

import org.joda.time.LocalDate;

/**
 * Datastructure for displaying a calendar
 */
public class CalendarDay {

	public final LocalDate date;

	public final User user;

	private List<HourEntry> hourEntries;

	/**
	 * Constructor for {@link CalendarDay}. Also tries to find all the entered
	 * hours for this day.
	 * 
	 * @param date
	 * @param user
	 */
	public CalendarDay(LocalDate date, User user) {
		this.date = date;
		this.user = user;
	}

	public List<HourEntry> getHourEntries() {
		hourEntries = HourEntry.findAllForUserForDay(user, date);
		return Collections.unmodifiableList(hourEntries);
	}

}
