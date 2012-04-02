package util.datastructures.overview.week;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.datastructures.TotalsDay;

public class HourEntriesWeekTable {

	private final long userId;

	private final DateTime beginDate;

	private final DateTime endDate;

	public final List<AssignmentRow> assignments;

	public final List<TotalsDay> daysTotals;

	public HourEntriesWeekTable(DateTime currentDate, Long userId) {
		beginDate = DateTimeUtil.firstDateOfWeek(currentDate);
		endDate = DateTimeUtil.lastDateOfWeek(currentDate);
		this.userId = userId;

		assignments = new LinkedList<AssignmentRow>();
		daysTotals = new LinkedList<TotalsDay>();
		createEntriesPerAssignment();
		createDaysTotals();
	}

	private void createEntriesPerAssignment() {
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
	}

	private AssignmentRow getAssignmentRow(HourEntry entry,
			List<AssignmentRow> assignments) {
		for (AssignmentRow row : assignments) {
			if (row.assignment.equals(entry.assignment)) {
				return row;
			}
		}
		return null;
	}

	private void createDaysTotals() {
		DateTime indexDate = beginDate;
		while (indexDate.isBefore(endDate)) {
			List<TotalsDay> totalsToday = HourEntry
					.getTotalsForUserPerDayBetween(userId,
							DateTimeUtil.minimizeTimeOfDate(indexDate),
							DateTimeUtil.maximizeTimeOfDate(indexDate));
			
			if (totalsToday.isEmpty()) {
				daysTotals.add(new TotalsDay(indexDate, 0L, 0L));
			} else {
				daysTotals.add(totalsToday.get(0));
			}
			indexDate = indexDate.plusDays(1);
		}
	}
}
