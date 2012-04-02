package util.datastructures.overview.week;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

public class DayField {
	
	public final DateTime date;
	
	public final List<HourEntry> entries;
	
	public DayField(DateTime date) {
		this.date = date;
		entries = new LinkedList<HourEntry>();
	}

}
