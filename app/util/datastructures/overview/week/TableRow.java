package util.datastructures.overview.week;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;
import models.ProjectAssignment;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.datastructures.TotalsAssignment;

/**
 * Datastructure for displaying a week with all entered hours. TableRow
 * represent a {@link ProjectAssignment}
 */
public class TableRow {

	public final ProjectAssignment assignment;

	public final List<WeekDay> days;

	public final TotalsAssignment assignmentTotals;

	/**
	 * Creates 7 days. Also calculates the totals for this assignment for this
	 * week.
	 * 
	 * @param assignment
	 *            The assignment which is represented
	 * @param firstDateOfWeek
	 *            The first date of the represented week
	 */
	public TableRow(ProjectAssignment assignment, DateTime firstDateOfWeek) {
		this.assignment = assignment;
		days = new LinkedList<WeekDay>();

		DateTime firstDateNextWeek = firstDateOfWeek.plusDays(7);
		DateTime indexDate = firstDateOfWeek;
		while (indexDate.isBefore(firstDateNextWeek)) {
			days.add(new WeekDay(indexDate));
			indexDate = indexDate.plusDays(1);
		}

		assignmentTotals = HourEntry.getTotalsForAssignmentBetween(
				assignment.id, firstDateOfWeek,
				DateTimeUtil.lastDateOfWeek(firstDateOfWeek));
	}

	public WeekDay getWeekDayForDate(DateTime date) {
		for (WeekDay dayField : days) {
			if (dayField.date.equals(date)) {
				return dayField;
			}
		}
		return null;
	}

}
