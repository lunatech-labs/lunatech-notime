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

import org.joda.time.LocalDate;

import util.DateUtil;
import util.Predicates;

import com.google.common.collect.Collections2;

import datastructures.TotalsAssignment;
import datastructures.TotalsDay;
import formbeans.WeekHourEntryBean;

public class Week {

	private final List<WeekHourEntryBean> hourEntries;

	private int weekyear;

	private int weekOfWeekyear;

	private final Map<WeekHourEntryBean, List<String>> errors;

	private boolean valid;

	public Week() {
		hourEntries = new LinkedList<WeekHourEntryBean>();
		errors = new HashMap<WeekHourEntryBean, List<String>>();
	}

	public Week(final Long userId, final int weekyear, final int weekOfWeekyear) {
		hourEntries = new LinkedList<WeekHourEntryBean>();
		this.weekyear = weekyear;
		this.weekOfWeekyear = weekOfWeekyear;
		fillHourEntries(userId);
		errors = new HashMap<WeekHourEntryBean, List<String>>();
	}

	public List<WeekHourEntryBean> getHourEntries() {
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
	public Set<ProjectAssignment> getAssignments(final Long userId) {
		Set<ProjectAssignment> assignments = new TreeSet<ProjectAssignment>();
		for (WeekHourEntryBean hourEntry : hourEntries) {
			assignments.add(hourEntry.assignment);
		}
		for (ProjectAssignment assignment : ProjectAssignment
				.findAllStarredForUser(userId)) {
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
	 * @return A List of {@link WeekHourEntryBean}
	 */
	public List<WeekHourEntryBean> getHourEntries(
			final ProjectAssignment assignment, final LocalDate day) {
		final List<WeekHourEntryBean> hourEntries = new LinkedList<WeekHourEntryBean>();
		Collection<WeekHourEntryBean> filtered = Collections2.filter(
				this.hourEntries,
				Predicates.equalAssignmentAndDay(assignment, day));
		hourEntries.addAll(filtered);
		return hourEntries;
	}

	/**
	 * Get the totals of a Day for a user
	 * 
	 * @param userId
	 *            The id of the user for which the totals have to be calculated
	 * @param day
	 *            The day for which the totals have to be calculated
	 * @return A {@link TotalsDay} with the totals
	 */
	public TotalsDay getDayTotals(final Long userId, final LocalDate day) {
		return HourEntry.findTotalsForUserForDay(userId, day);
	}

	/**
	 * Get the totals of an {@link ProjectAssignment} for this week
	 * 
	 * @param assignmentId
	 *            The id of the assignment which totals have to be calculated
	 * @return A {@link TotalsAssignment} with the totals
	 */
	public TotalsAssignment getAssignmentTotals(final Long assignmentId) {
		final LocalDate date = getDateThisWeek();
		final LocalDate firstDateThisWeek = DateUtil.firstDateOfWeek(date);
		final LocalDate lastDateThisWeek = DateUtil.lastDateOfWeek(date);
		return HourEntry.findTotalsForAssignmentBetween(assignmentId,
				firstDateThisWeek, lastDateThisWeek);
	}

	public Map<WeekHourEntryBean, List<String>> getErrors() {
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
	 * Retrieves all {@link WeekHourEntryBean} for the represented week from the
	 * database
	 * 
	 * @param date
	 *            A date in the represented week
	 */
	private void fillHourEntries(final Long userId) {
		final LocalDate date = getDateThisWeek();
		final LocalDate firstDateThisWeek = DateUtil.firstDateOfWeek(date);
		final LocalDate lastDateThisWeek = DateUtil.lastDateOfWeek(date);
		List<HourEntry> hourEntryModels = HourEntry.findAllForUserBetween(
				userId, firstDateThisWeek, lastDateThisWeek);
		for (HourEntry entry : hourEntryModels) {
			hourEntries.add(new WeekHourEntryBean(entry));
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
			ListIterator<WeekHourEntryBean> iterator = hourEntries
					.listIterator();
			while (iterator.hasNext()) {
				WeekHourEntryBean entry = (WeekHourEntryBean) iterator.next();
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
	public void process(Long userId) {
		if (valid) {
			ListIterator<WeekHourEntryBean> iterator = hourEntries
					.listIterator();
			while (iterator.hasNext()) {
				WeekHourEntryBean entry = (WeekHourEntryBean) iterator.next();
				// Check if id is null
				if (entry.id == null) {
					// Check if hours or minutes is not 0
					if (entry.hasNot0Hours()
							|| entry.hasNot0Minutes()) {
						entry.setHoursAndMinutesFromNullToZero();
						// Validate entry
						if (entry.isValid()) {
							entry.toHourEntry().save("");
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
							entry.toHourEntry().update(entry.id, "");
						}
					} else {
						// Check if user is allowed to delete this entry
						HourEntry hourEntry = HourEntry.findById(entry.id);
						if (hourEntry.assignment.user.id.equals(userId)) {
							hourEntry.delete();
							iterator.remove();
						}
					}
				}
			}
		}
	}

}
