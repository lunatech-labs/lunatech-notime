package util.datastructures.calendar;

import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

import util.DateTimeUtil;

/*
 *  Datastructure for displaying a calendar
 */
public class CalendarDay {

	public DateTime date;

	public List<HourEntry> hourEntries;

	public CalendarDay(DateTime date, Long userId) {
		this.date = date;
		hourEntries = HourEntry.allForUserBetween(userId,
				DateTimeUtil.minimizeTimeOfDate(date),
				DateTimeUtil.maximizeTimeOfDate(date));
	}

}
