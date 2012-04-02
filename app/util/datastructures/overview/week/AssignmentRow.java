package util.datastructures.overview.week;

import java.util.LinkedList;
import java.util.List;

import models.HourEntry;
import models.ProjectAssignment;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.datastructures.TotalsAssignment;

public class AssignmentRow {
	
	public final ProjectAssignment assignment;
	
	public final List<DayField> days;
	
	public final TotalsAssignment assignmentTotals;
	
	public AssignmentRow(ProjectAssignment assignment, DateTime firstDateOfWeek) {
		this.assignment = assignment;
		days = new LinkedList<DayField>();
		
		DateTime firstDateNextWeek = firstDateOfWeek.plusDays(7);
		DateTime indexDate = firstDateOfWeek;
		while (indexDate.isBefore(firstDateNextWeek)) {
			days.add(new DayField(indexDate));
			indexDate = indexDate.plusDays(1);
		}
		
		assignmentTotals = HourEntry.getTotalsForAssignmentBetween(assignment.id, firstDateOfWeek, DateTimeUtil.lastDateOfWeek(firstDateOfWeek));
	}

	public DayField getDayField(DateTime date) {
		for (DayField dayField : days) {
			if (dayField.date.equals(date)) {
				return dayField; 
			}
		}
		return null;
	}

}
