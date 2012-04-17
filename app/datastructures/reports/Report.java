package datastructures.reports;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import models.Customer;
import models.HourEntry;
import models.Project;

import org.joda.time.LocalDate;

import utils.CalculationUtil;
import utils.predicates.HourEntryPredicates;
import utils.predicates.ProjectPredicates;

import com.google.common.collect.Collections2;

public class Report {

	private final List<HourEntry> hourEntries;

	private Report(final List<HourEntry> hourEntries) {
		this.hourEntries = hourEntries;
	}

	public static Report getOverviewPerWeek(final Set<Project> projects,
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

	public static Report getEmptyReport() {
		final List<HourEntry> hourEntries = Collections.emptyList();
		return new Report(hourEntries);
	}

	/**
	 * Get all unique {@link Customer}s from this report
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
	 * Get all unique {@link Project}s from this report
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
	 * Get all unique {@link Project}s from this report for a {@link Customer}
	 * 
	 * @param customer
	 *            The customer to which the entries customer must be equal to
	 * @return List of {@link Project}s
	 */
	public List<Project> getProjects(final Customer customer) {
		final List<Project> projects = new LinkedList<Project>();
		final Collection<Project> filtered = Collections2.filter(getProjects(),
				ProjectPredicates.equalCustomer(customer));
		projects.addAll(filtered);
		return projects;
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
	 * Get all {@link HourEntry}s where the project and weekNumber are equal to
	 * the provided project and weekNumber
	 * 
	 * @param project
	 *            The project to which the entries must be equal to.
	 * @param weekNumber
	 *            The week number to which the entries must be equal to.
	 * @return List of {@link HourEntry}s
	 */
	public List<HourEntry> getHourEntries(final Project project,
			final int weekNumber) {
		final List<HourEntry> hourEntries = new LinkedList<HourEntry>();
		final Collection<HourEntry> filtered = Collections2.filter(
				this.hourEntries, HourEntryPredicates
						.equalProjectAndWeekNumber(project, weekNumber));
		hourEntries.addAll(filtered);
		return hourEntries;
	}

	/**
	 * Get the total hours and minutes for a project for a week
	 * 
	 * @param project
	 *            The project of which the total must be calculated
	 * @param weekNumber
	 *            The week number of which the total must be calculated
	 * @return A String with the total hours and minutes in the format 4h 25m
	 */
	public String getTotalHours(final Project project, final int weekNumber) {
		final List<HourEntry> hourEntries = getHourEntries(project, weekNumber);
		int hours = 0;
		int minutes = 0;
		for (HourEntry hourEntry : hourEntries) {
			hours += hourEntry.hours;
			minutes += hourEntry.minutes;
		}
		return CalculationUtil.formatTotalHoursMinutes(hours, minutes);
	}

}
