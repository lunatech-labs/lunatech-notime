package formbeans;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;
import models.ProjectAssignment;
import models.Tag;

import org.joda.time.LocalDate;

import util.DateUtil;

public class WeekHourEntryBean {

	public Long id;

	public ProjectAssignment assignment;

	public LocalDate date;

	public Integer hours;

	public Integer minutes;

	public List<Tag> tags;

	private final List<String> errors;

	private boolean valid;

	public WeekHourEntryBean() {
		errors = new LinkedList<String>();
	}

	public WeekHourEntryBean(HourEntry entry) {
		id = entry.id;
		assignment = entry.assignment;
		date = entry.date;
		hours = entry.hours;
		minutes = entry.minutes;
		tags = entry.tags;
		errors = new LinkedList<String>();
	}

	/**
	 * Checks if hours or minutes has null as value. if true it replaces it with
	 * zero
	 */
	public void setHoursAndMinutesFromNullToZero() {
		if (hours == null)
			hours = 0;
		if (minutes == null)
			minutes = 0;
	}

	/**
	 * A entry is valid if it has a date, assignment, hours and minutes. And the
	 * hours are less than 24 and minutes less than 60. This method does all
	 * validations. It doesn't stop at the first error.
	 * 
	 * @return true is the entry is valid
	 */
	public boolean isValid() {
		valid = true;
		validate();
		return valid;
	}

	private void validate() {
		hasDate();
		hasAssignment();
		hasHours();
		hasMinutes();
		hasLessThan24Hours();
		hasLessThan60Minutes();
		isDateInAssignmentRange();
	}

	public boolean hasDate() {
		if (date == null) {
			errors.add("Invalid input for date");
			valid = false;
			return false;
		}
		return true;
	}

	public boolean hasAssignment() {
		if (assignment == null || assignment.id == null) {
			errors.add("Invalid input for assignment");
			valid = false;
			return false;
		}
		assignment = ProjectAssignment.findById(assignment.id);
		return true;
	}

	public boolean isDateInAssignmentRange() {
		if (!ProjectAssignment.isDateInAssignmentRange(date, assignment.id)) {
			errors.add("Invalid input, you are assigned to "
					+ assignment.project.code + " untill "
					+ DateUtil.formatDate(assignment.endDate, "E d-M"));
			valid = false;
			return false;
		}
		return true;
	}

	public boolean hasMoreThan0Hours() {
		return hours != null && hours > 0;
	}

	public boolean hasMoreThan0Minutes() {
		return minutes != null && minutes > 0;
	}

	public boolean hasHours() {
		if (hours == null) {
			errors.add("Invalid input for hours");
			valid = false;
			return false;
		}
		return true;
	}

	public boolean hasMinutes() {
		if (minutes == null) {
			errors.add("Invalid input for minutes");
			valid = false;
			return false;
		}
		return true;
	}

	public boolean hasLessThan24Hours() {
		if (!hasHours() || hours >= 24) {
			errors.add("Hours can't be more than 23");
			valid = false;
			return false;
		}
		return true;
	}

	public boolean hasLessThan60Minutes() {
		if (!hasMinutes() || minutes >= 60) {
			errors.add("Minutes can't be more than 59");
			valid = false;
			return false;
		}
		return true;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
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
