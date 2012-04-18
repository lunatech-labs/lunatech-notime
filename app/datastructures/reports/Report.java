package datastructures.reports;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Customer;
import models.HourEntry;
import models.Project;
import models.ProjectAssignment;
import models.User;

import org.joda.time.LocalDate;

import utils.CalculationUtil;
import utils.predicates.HourEntryPredicates;
import utils.predicates.ProjectPredicates;

import com.google.common.collect.Collections2;

import datastructures.TotalsAssignment;

public class Report {

	private final List<HourEntry> hourEntries;

	private Report(final List<HourEntry> hourEntries) {
		this.hourEntries = hourEntries;
	}

	public static Report getEmptyReport() {
		final List<HourEntry> hourEntries = Collections.emptyList();
		return new Report(hourEntries);
	}

	/**
	 * Get a report for the entries per week
	 * 
	 * @param projects
	 *            The projects for which the report must be created
	 * @param beginDate
	 *            The date form which the report is created
	 * @param endDate
	 *            The date till which the report is created (the entries on
	 *            endDate will also be included)
	 * @return A {@link Report}
	 */
	public static Report getReportEntriesPerCustomerPerProjectPerWeek(final Set<Project> projects,
			final LocalDate beginDate, final LocalDate endDate) {
		List<HourEntry> hourEntries = Collections.emptyList();
		if (projects.isEmpty()) {
			hourEntries = new LinkedList<HourEntry>();
		} else {
			hourEntries = HourEntry.findAllForProjectsBetween(projects,
					beginDate, endDate);
		}
		return new Report(hourEntries);
	}

	/**
	 * Get a report for the assignment totals per user
	 * 
	 * @param users
	 *            The users for which the report must be created
	 * @param beginDate
	 *            The date form which the report is created
	 * @param endDate
	 *            The date till which the report is created (the entries on
	 *            endDate will also be included)
	 * @return A {@link Report}
	 */
	public static Report getReportAssignmentTotalsPerUser(
			final Set<User> users, final LocalDate beginDate,
			final LocalDate endDate) {
		final List<HourEntry> hourEntries = HourEntry.findAllForUsersBetween(
				users, beginDate, endDate);
		return new Report(hourEntries);
	}

	/**
	 * Get all unique {@link Customer}s in this report
	 * 
	 * @return Set of {@link Customer}s
	 */
	public Set<Customer> getCustomers() {
		final Set<Customer> customers = new HashSet<Customer>();
		for (HourEntry entry : hourEntries) {
			customers.add(entry.assignment.project.customer);
		}
		return customers;
	}

	/**
	 * Get all unique {@link Project}s in this report
	 * 
	 * @return Set of {@link Project}s
	 */
	public Set<Project> getProjects() {
		final Set<Project> projects = new HashSet<Project>();
		for (HourEntry entry : hourEntries) {
			projects.add(entry.assignment.project);
		}
		return projects;
	}

	/**
	 * Get all unique {@link User}s in this report
	 * 
	 * @return Set of {@link User}s
	 */
	public Set<User> getUsers() {
		final Set<User> users = new HashSet<User>();
		for (HourEntry entry : hourEntries) {
			users.add(entry.assignment.user);
		}
		return users;
	}

	/**
	 * Get all {@link HourEntry}s
	 * 
	 * @return List of {@link HourEntry}s
	 */
	public List<HourEntry> getHourEntries() {
		return hourEntries;
	}

	/**
	 * Get all unique {@link Project}s from this report for a {@link Customer}
	 * 
	 * @param customer
	 *            The customer to which the entries customer must be equal to
	 * @return List of {@link Project}s
	 */
	public List<Project> getProjects(final Customer customer) {
		final Collection<Project> filtered = Collections2.filter(getProjects(),
				ProjectPredicates.equalCustomer(customer));
		return new LinkedList<Project>(filtered);
	}

	/**
	 * Get all {@link HourEntry}s where the user is equal to the provided user
	 * 
	 * @param user
	 *            The user to which the entry's user must be equal to
	 * @return List of {@link HourEntry}s
	 */
	public List<HourEntry> getHourEntries(final User user) {
		final Collection<HourEntry> filtered = Collections2.filter(
				this.hourEntries, HourEntryPredicates.equalUser(user));
		return new LinkedList<HourEntry>(filtered);
	}

	/**
	 * Get all {@link HourEntry}s where the project and weekNumber are equal to
	 * the provided project and weekNumber
	 * 
	 * @param project
	 *            The project to which the entry's project must be equal to
	 * @param weekNumber
	 *            The week number to which the entry's week number must be equal
	 *            to
	 * @return List of {@link HourEntry}s
	 */
	public List<HourEntry> getHourEntries(final Project project,
			final int weekNumber) {
		final Collection<HourEntry> filtered = Collections2.filter(
				this.hourEntries, HourEntryPredicates
						.equalProjectAndWeekNumber(project, weekNumber));
		return new LinkedList<HourEntry>(filtered);
	}

	/**
	 * Get all {@link HourEntry}s where the user is to the provided user and the
	 * entry's date is between the provided dates.
	 * 
	 * @param user
	 *            The user to which the entry's user must be equal to
	 * @param beginDate
	 *            The date from which entries are filtered
	 * @param endDate
	 *            The date till which entries are filtered (the entries where
	 *            date is equal to endDate will be added)
	 * @return List of {@link HourEntry}s
	 */
	public List<HourEntry> getHourEntries(final User user,
			final LocalDate beginDate, final LocalDate endDate) {
		final Collection<HourEntry> filtered = Collections2.filter(
				this.hourEntries, HourEntryPredicates.equalUserAndBetweenDates(
						user, beginDate, endDate.plusDays(1)));
		return new LinkedList<HourEntry>(filtered);
	}

	/**
	 * Get all {@link TotalsAssignment}s where the {@link HourEntry}'s user is
	 * equal to the provided user
	 * 
	 * @param user
	 *            The user to which the entry's user must be equal to
	 * @return List of {@link TotalsAssignment}s
	 */
	public List<TotalsAssignment> getAssignmentsTotals(User user) {
		final Map<ProjectAssignment, TotalsAssignment> totalsPerAssignment = new HashMap<ProjectAssignment, TotalsAssignment>();

		// Map HourEntries
		for (HourEntry entry : getHourEntries(user)) {
			if (totalsPerAssignment.containsKey(entry.assignment)) {
				totalsPerAssignment.get(entry.assignment).addHoursMinutes(
						entry.hours, entry.minutes);
			} else {
				TotalsAssignment totals = new TotalsAssignment(
						entry.assignment, (long) entry.hours,
						(long) entry.minutes);
				totalsPerAssignment.put(entry.assignment, totals);
			}

		}

		return new LinkedList<TotalsAssignment>(totalsPerAssignment.values());
	}

	/**
	 * Get the total hours and minutes for a project for a week
	 * 
	 * @param project
	 *            The project which totals must be calculated
	 * @param weekNumber
	 *            The week number which totals must be calculated
	 * @return A String with the total hours and minutes in the format 4h 25m
	 */
	public String getTotalHours(final Project project, final int weekNumber) {
		int hours = 0;
		int minutes = 0;
		for (HourEntry entry : getHourEntries(project, weekNumber)) {
			hours += entry.hours;
			minutes += entry.minutes;
		}
		return CalculationUtil.formatTotalHoursMinutes(hours, minutes);
	}

	/**
	 * Get the total hours and minutes for a user in a date range
	 * 
	 * @param user
	 *            The user which totals must be calculated
	 * @param beginDate
	 *            The date from which the totals must be calculated
	 * @param endDate
	 *            The date till which the totals must be calculated (entries
	 *            where date is equal to endDate will also be included in the
	 *            calculation)
	 * @return A String with the total hours and minutes in the format 4h 25m
	 */
	public String getTotalHours(final User user, final LocalDate beginDate,
			final LocalDate endDate) {
		int hours = 0;
		int minutes = 0;
		for (HourEntry entry : getHourEntries(user, beginDate, endDate)) {
			hours += entry.hours;
			minutes += entry.minutes;
		}
		return CalculationUtil.formatTotalHoursMinutes(hours, minutes);
	}

	/**
	 * Get the total turnover for a user in a date range
	 * 
	 * @param user
	 *            The user which total must be calculated
	 * @param beginDate
	 *            The date from which the total must be calculated
	 * @param endDate
	 *            The date till which the total must be calculated (entries
	 *            where date is equal to endDate will also be included in the
	 *            calculation)
	 * @return A {@link BigDecimal} with the total turnover
	 */
	public BigDecimal getTotalTurnover(final User user,
			final LocalDate beginDate, final LocalDate endDate) {
		BigDecimal totalTurnover = BigDecimal.ZERO;
		for (TotalsAssignment totals : getAssignmentsTotals(user)) {
			totalTurnover = totalTurnover.add(totals.turnover);
		}
		return totalTurnover;
	}
}
