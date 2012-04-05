package datastructures.overview.week;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

/**
 * Datastructure for displaying a week with all entered hours
 */
public class WeekDay {
	
	public final DateTime date;
	
	public final List<HourEntry> entries;
	
	public WeekDay(DateTime date) {
		this.date = date;
		entries = new LinkedList<HourEntry>();
	}

}
