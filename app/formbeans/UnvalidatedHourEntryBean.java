package formbeans;

import java.util.List;

import models.HourEntry;
import models.ProjectAssignment;
import models.Tag;

import org.joda.time.DateTime;

public class UnvalidatedHourEntryBean {

	public Long id;

	public ProjectAssignment assignment;

	public DateTime date;

	public Integer hours;

	public Integer minutes;

	public List<Tag> tags;

	/**
	 * Checks if hours or minutes has null as value. if true it replaces it with
	 * zero
	 */
	public void setHoursAndMinutesFromNullToZero() {
		if (!hasHours())
			hours = 0;
		if (!hasMinutes())
			minutes = 0;
	}

	public boolean isValid() {
		return hasDate() && hasHours() && hasMinutes() && hasValidHours()
				&& hasValidMinutes();
	}

	public boolean hasDate() {
		return date != null;
	}
	
	public boolean hasNullOrZeroHours() {
		return !hasHours() || hasZeroHours();
	}
	
	public boolean hasNullOrZeroMinutes() {
		return !hasMinutes() || hasZeroMinutes();
	}

	public boolean hasHours() {
		return hours != null;
	}

	public boolean hasMinutes() {
		return minutes != null;
	}
	
	public boolean hasZeroHours() {
		return hours == 0;
	}

	public boolean hasZeroMinutes() {
		return minutes == 0;
	}

	public boolean hasValidHours() {
		return hours < 24;
	}

	public boolean hasValidMinutes() {
		return minutes < 60;
	}
	
	public HourEntry toHourEntry() {
		HourEntry entry = new HourEntry();
		entry.id = id;
		entry.assignment = assignment;
		entry.date = date;
		entry.hours = hours;
		entry.minutes = minutes;
		entry.tags = tags;		
		return entry;
	}

}
