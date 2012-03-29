package util.datastructures.weekoverview;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.datastructures.TotalsDay;

public class HourEntriesWeekTable {

	public final List<AssignmentRow> assignments;

	public final List<TotalsDay> daysTotals;

	public HourEntriesWeekTable(DateTime currentDate, Long userId) {
		DateTime beginDate = DateTimeUtil.firstDateOfWeek(currentDate);
		DateTime endDate = DateTimeUtil.lastDateOfWeek(currentDate);

		assignments = createEntriesPerAssignment(userId, beginDate, endDate);
		daysTotals = HourEntry.getTotalsForUserPerDayBetween(userId, beginDate,
				endDate);

	}

	private List<AssignmentRow> createEntriesPerAssignment(Long userId,
			DateTime beginDate, DateTime endDate) {
		LinkedList<AssignmentRow> assignments = new LinkedList<AssignmentRow>();
		List<HourEntry> entries = HourEntry.findAllForUserBetween(userId,
				beginDate, endDate);

		for (HourEntry entry : entries) {
			AssignmentRow row = getAssignmentRow(entry, assignments);
			if (row == null) {
				row = new AssignmentRow(entry.assignment, beginDate);
				assignments.add(row);
			}

			DayField day = row.getDayField(entry.date);
			day.entries.add(entry);
		}
		return assignments;
	}

	private AssignmentRow getAssignmentRow(HourEntry entry, List<AssignmentRow> assignments) {
		for (AssignmentRow row : assignments) {
			if (row.assignment.equals(entry.assignment)) {
				return row;
			}
		}
		return null;
	}
}
