package util.datastructures.overview.calendar;

import java.util.Collections;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

import util.DateTimeUtil;

/**
 * Datastructure for displaying a calendar
 */
public class CalendarDay {

	public final DateTime date;
	
	public final Long userId;

	private List<HourEntry> hourEntries;

	/**
	 * Constructor for {@link CalendarDay}. Also tries to find all the entered
	 * hours for this day.
	 * 
	 * @param date
	 * @param userId
	 */
	public CalendarDay(DateTime date, Long userId) {
		this.date = date;
		this.userId = userId;
	}
	
	public List<HourEntry> getHourEntries() {
		hourEntries = HourEntry.findAllForUserBetween(userId,
				DateTimeUtil.minimizeTimeOfDate(date),
				DateTimeUtil.maximizeTimeOfDate(date));
		return Collections.unmodifiableList(hourEntries);
	}
	

}
