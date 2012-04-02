package util.datastructures.overview.week;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.datastructures.TotalsDay;

/**
 * Datastructure for displaying a week with all entered hours
 */
public class WeekTable {

	private final long userId;

	private final DateTime beginDate;

	private final DateTime endDate;

	public final List<TableRow> assignmentRows;

	public final List<TotalsDay> daysTotals;

	/**
	 * Creates the week, which contains assignments, which contains days, which
	 * contains multiple {@link HourEntry}. The {@link WeekTable} also a List of
	 * {@link TotalsDay} which is also filled in the constructor
	 * 
	 * @param date
	 *            A date in the represented week
	 * @param userId
	 *            The id of the user
	 */
	public WeekTable(DateTime date, Long userId) {
		beginDate = DateTimeUtil.firstDateOfWeek(date);
		endDate = DateTimeUtil.lastDateOfWeek(date);
		this.userId = userId;

		assignmentRows = new LinkedList<TableRow>();
		daysTotals = new LinkedList<TotalsDay>();
		createRowsAndDaysAndFillWithEntries();
		createDaysTotals();
	}

	/**
	 * Gets all the entries for this user, in this week, from the database. And
	 * put them in the right days. If the day or the assignment does not exist
	 * yet, it is created
	 */
	private void createRowsAndDaysAndFillWithEntries() {
		List<HourEntry> entries = HourEntry.findAllForUserBetween(userId,
				beginDate, endDate);

		for (HourEntry entry : entries) {
			TableRow row = getOrCreateRow(entry);
			WeekDay day = row.getWeekDayForDate(entry.date);
			day.entries.add(entry);
		}
	}

	private TableRow getOrCreateRow(HourEntry entry) {
		TableRow row = getRow(entry);
		if (row == null) {
			row = createRow(entry);
		}
		return row;
	}

	private TableRow getRow(HourEntry entry) {
		for (TableRow row : assignmentRows) {
			if (row.assignment.equals(entry.assignment)) {
				return row;
			}
		}
		return null;
	}

	private TableRow createRow(HourEntry entry) {
		TableRow row = new TableRow(entry.assignment, beginDate);
		assignmentRows.add(row);
		return row;
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
