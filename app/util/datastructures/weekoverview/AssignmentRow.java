package util.datastructures.weekoverview;

import java.util.LinkedList;
import java.util.List;

import models.ProjectAssignment;

import org.joda.time.DateTime;

public class AssignmentRow {
	
	public final ProjectAssignment assignment;
	
	public final List<DayField> dayFields;
	
	public AssignmentRow(ProjectAssignment assignment, DateTime firstDateOfWeek) {
		this.assignment = assignment;
		dayFields = new LinkedList<DayField>();
		
		DateTime firstDateNextWeek = firstDateOfWeek.plusDays(7);
		DateTime indexDate = firstDateOfWeek;
		while (indexDate.isBefore(firstDateNextWeek)) {
			dayFields.add(new DayField(indexDate));
			indexDate = indexDate.plusDays(1);
		}
	}

	public DayField getDayField(DateTime date) {
		for (DayField dayField : dayFields) {
			if (dayField.date.equals(date)) {
				return dayField; 
			}
		}
		return null;
	}

}
