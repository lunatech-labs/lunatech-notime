package util.datastructures.weekoverview;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;

import org.joda.time.DateTime;

import util.DateTimeUtil;

public class HourEntriesWeekTable {

	public final List<AssignmentRow> assignmentRows;

	public HourEntriesWeekTable(DateTime currentDate, Long userId) {
		assignmentRows = new LinkedList<AssignmentRow>();
		createEntriesPerAssignment(DateTimeUtil.firstDateOfWeek(currentDate),
				userId);
	}

	private void createEntriesPerAssignment(
			DateTime date, Long userId) {
		DateTime firstDateOfWeek = DateTimeUtil.firstDateOfWeek(date);
		DateTime lastDateOfWeek = DateTimeUtil.lastDateOfWeek(date);
		List<HourEntry> entries = HourEntry.findAllForUserBetween(userId,
				firstDateOfWeek, lastDateOfWeek);
		
		for (HourEntry entry : entries) {
			AssignmentRow row = getAssignmentRow(entry);
			if (row == null) {
				row = new AssignmentRow(entry.assignment, firstDateOfWeek);
				assignmentRows.add(row);
			}

			DayField day = row.getDayField(entry.date);
			day.entries.add(entry);			
		}
	}

	private AssignmentRow getAssignmentRow(HourEntry entry) {
		for (AssignmentRow row : assignmentRows) {
			if (row.assignment.equals(entry.assignment)) {
				return row;
			}
		}
		return null;
	}
}
