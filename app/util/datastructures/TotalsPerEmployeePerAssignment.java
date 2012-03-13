package util.datastructures;

import java.math.BigDecimal;

import util.CalculationUtil;

import models.ProjectAssignment;

public class TotalsPerEmployeePerAssignment {

	public ProjectAssignment assignment;

	public long hours;

	public long minutes;

	public String hoursMinutes;
	
	public BigDecimal turnover;

	public TotalsPerEmployeePerAssignment(ProjectAssignment assignment,
			long hours, long minutes) {
		this.assignment = assignment;
		this.minutes = minutes;
		this.hours = hours;
		hoursMinutes = CalculationUtil.totalHoursMinutesFormatter(hours,
				minutes);
		turnover = CalculationUtil.totalTurnover(hours, minutes,
				assignment.hourlyRate);
	}
}
