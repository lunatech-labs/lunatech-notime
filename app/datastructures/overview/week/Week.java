package datastructures.overview.week;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.HourEntry;
import models.ProjectAssignment;
import models.User;

import org.joda.time.LocalDate;

import utils.DateUtil;
import utils.predicates.HourEntryInWeekPredicates;
import beans.HourEntryInWeek;

import com.google.common.collect.Collections2;

public class Week {

	private final List<HourEntryInWeek> hourEntries;

	private int weekyear;

	private int weekOfWeekyear;

	private User user;

	private final Map<HourEntryInWeek, List<String>> errors;

	private boolean valid;

	public Week() {
		hourEntries = new LinkedList<HourEntryInWeek>();
		errors = new HashMap<HourEntryInWeek, List<String>>();
	}

	public Week(final User user, final int weekyear, final int weekOfWeekyear) {
		hourEntries = new LinkedList<HourEntryInWeek>();
		this.user = user;
		this.weekyear = weekyear;
		this.weekOfWeekyear = weekOfWeekyear;
		fillHourEntries();
		errors = new HashMap<HourEntryInWeek, List<String>>();
	}

	public List<HourEntryInWeek> getHourEntries() {
		return hourEntries;
	}

	public List<LocalDate> getDays() {
		return createDays();
	}

	/**
	 * Get all the unique assignments out of the hourEntries
	 * 
	 * @return A {@link Set} of {@link ProjectAssignment}s
	 */
	public Set<ProjectAssignment> getAssignments() {
		Set<ProjectAssignment> assignments = new TreeSet<ProjectAssignment>();
		for (HourEntryInWeek hourEntry : hourEntries) {
			assignments.add(hourEntry.assignment);
		}
		for (ProjectAssignment assignment : ProjectAssignment
				.findAllStarredForUser(user)) {
			assignments.add(assignment);
		}
		return assignments;
	}

	/**
	 * Get the entries for an assignment on a day
	 * 
	 * @param assignment
	 *            The assignment which entries are returned
	 * @param day
	 *            The day which entries are returned
	 * @return A List of {@link HourEntryInWeek}
	 */
	public List<HourEntryInWeek> getHourEntries(
			final ProjectAssignment assignment, final LocalDate day) {
		final List<HourEntryInWeek> hourEntries = new LinkedList<HourEntryInWeek>();
		Collection<HourEntryInWeek> filtered = Collections2.filter(
				this.hourEntries,
				HourEntryInWeekPredicates.equalAssignmentAndDay(assignment, day));
		hourEntries.addAll(filtered);
		return hourEntries;
	}

	public Map<HourEntryInWeek, List<String>> getErrors() {
		return errors;
	}

	public int getWeekyear() {
		return weekyear;
	}

	public void setWeekyear(int weekyear) {
		this.weekyear = weekyear;
	}

	public int getWeekOfWeekyear() {
		return weekOfWeekyear;
	}

	public void setWeekOfWeekyear(int weekOfWeekyear) {
		this.weekOfWeekyear = weekOfWeekyear;
	}

	private LocalDate getDateThisWeek() {
		return new LocalDate().withWeekyear(weekyear).withWeekOfWeekyear(
				weekOfWeekyear);
	}

	private LocalDate getDatePreviousWeek() {
		return getDateThisWeek().minusWeeks(1);
	}

	public int getWeekyearPreviousWeek() {
		final LocalDate date = getDatePreviousWeek();
		return date.getWeekyear();
	}

	public int getWeekOfWeekyearPreviousWeek() {
		final LocalDate date = getDatePreviousWeek();
		return date.getWeekOfWeekyear();
	}

	private LocalDate getDateNextWeek() {
		return getDateThisWeek().plusWeeks(1);
	}

	public int getWeekyearNextWeek() {
		final LocalDate date = getDateNextWeek();
		return date.getWeekyear();
	}

	public int getWeekOfWeekyearNextWeek() {
		final LocalDate date = getDateNextWeek();
		return date.getWeekOfWeekyear();
	}

	/**
	 * Retrieves all {@link HourEntryInWeek} for the represented week from the
	 * database
	 * 
	 * @param date
	 *            A date in the represented week
	 */
	private void fillHourEntries() {
		final LocalDate date = getDateThisWeek();
		final LocalDate firstDateThisWeek = DateUtil.firstDateOfWeek(date);
		final LocalDate lastDateThisWeek = DateUtil.lastDateOfWeek(date);
		List<HourEntry> hourEntryModels = HourEntry.findAllForUserBetween(
				user, firstDateThisWeek, lastDateThisWeek);
		for (HourEntry entry : hourEntryModels) {
			hourEntries.add(new HourEntryInWeek(entry));
		}
	}

	/**
	 * Creates the days in the represented week
	 * 
	 * @param date
	 *            A date in the represented week
	 */
	public List<LocalDate> createDays() {
		final List<LocalDate> days = new LinkedList<LocalDate>();

		final LocalDate date = getDateThisWeek();
		final LocalDate firstDateThisWeek = DateUtil.firstDateOfWeek(date);
		final LocalDate firstDateNextWeek = firstDateThisWeek.plusDays(7);

		LocalDate indexDate = firstDateThisWeek;
		while (indexDate.isBefore(firstDateNextWeek)) {
			days.add(indexDate);
			indexDate = indexDate.plusDays(1);
		}

		return days;
	}

	/**
	 * Does not validate the week. Only returns it's property
	 * 
	 * @return the result of the last validation check
	 */
	public boolean isValid() {
		validate();
		return valid;
	}

	/**
	 * Validates all the binded data. First checks if there is anything binded.
	 * Second check is if the fields hours and minutes contain a value higher
	 * than 0. Third check if the entry contains valid data.
	 * 
	 * @return true if the binded data contains no errors
	 */
	private void validate() {
		valid = true;
		// 1. Check if anything is binded
		if (hourEntries != null) {
			ListIterator<HourEntryInWeek> iterator = hourEntries
					.listIterator();
			while (iterator.hasNext()) {
				HourEntryInWeek entry = (HourEntryInWeek) iterator.next();
				// 2. Check if hours or minutes is not 0
				if ((entry.hasNot0Hours() || entry.hasNot0Minutes())) {
					entry.setHoursAndMinutesFromNullToZero();
					// 3. Check if the new entry is valid or not
					if (!entry.isValid()) {
						valid = false;
						errors.put(entry, entry.getErrors());
					}
				} else {
					if (entry.id == null) {
						iterator.remove();
					}
				}
			}
		}
	}

	/**
	 * Processes the valid binded data. For each entry are 4 possible actions:
	 * 1. The id is null: 1.1. Hours & minutes are null or 0 -> do nothing. 1.2.
	 * Hours & minutes have a value -> validate input and create @{link
	 * HourEntry}. 2. The id is not null: 2.1. Hours & minutes are null or 0 ->
	 * delete @{link HourEntry}. 2.2. Hours & minutes have a value -> validate
	 * input and update.
	 */
	public void process(final User user) {
		this.user = user;
		if (valid) {
			ListIterator<HourEntryInWeek> iterator = hourEntries
					.listIterator();
			while (iterator.hasNext()) {
				HourEntryInWeek entry = (HourEntryInWeek) iterator.next();
				// Check if id is null
				if (entry.id == null) {
					// Check if hours or minutes is not 0
					if (entry.hasNot0Hours()
							|| entry.hasNot0Minutes()) {
						entry.setHoursAndMinutesFromNullToZero();
						// Validate entry
						if (entry.isValid()) {
							entry.toHourEntry().save();
						}
					} else {
						iterator.remove();
					}
				} else {
					// Check if hours or minutes is not 0
					if (entry.hasNot0Hours()
							|| entry.hasNot0Minutes()) {
						entry.setHoursAndMinutesFromNullToZero();
						// Validate entry
						if (entry.isValid()) {
							entry.toHourEntry().update(entry.id);
						}
					} else {
						// Check if user is allowed to delete this entry
						HourEntry hourEntry = HourEntry.findById(entry.id);
						if (hourEntry.assignment.user.equals(user)) {
							hourEntry.delete();
							iterator.remove();
						}
					}
				}
			}
		}
	}

}
