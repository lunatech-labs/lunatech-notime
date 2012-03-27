package util.datastructures.weekoverview;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DayField {
	
	public final DateTime date;
	
	public final List<HourEntry> entries;
	
	public DayField(DateTime date) {
		this.date = date;
		entries = new LinkedList<HourEntry>();
	}
	
	public String formattedDate() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		return date.toString(fmt);
	}

}
