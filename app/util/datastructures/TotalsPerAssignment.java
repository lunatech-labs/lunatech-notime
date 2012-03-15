package util.datastructures;

import java.math.BigDecimal;

import util.CalculationUtil;

import models.ProjectAssignment;

/**
 * Datastructure for displaying the totals for an assignment
 */
public class TotalsPerAssignment {

	public ProjectAssignment assignment;

	public long hours;

	public long minutes;

	public String hoursMinutes;

	public BigDecimal turnover;

	/**
	 * Constructor for {@link TotalsPerAssignment}. Also fills the hoursMinutes
	 * field with a formatted String of the hours and minutes. And calculates
	 * the turnover field
	 * 
	 * @param assignment
	 *            The assignment on which the totals are calculated
	 * @param hours
	 *            The total hours booked on this assignment
	 * @param minutes
	 *            The total minutes booked on this minutes
	 */
	public TotalsPerAssignment(ProjectAssignment assignment, long hours,
			long minutes) {
		this.assignment = assignment;
		this.minutes = minutes;
		this.hours = hours;
		hoursMinutes = CalculationUtil.totalHoursMinutesFormatter(hours,
				minutes);
		turnover = CalculationUtil.totalTurnover(hours, minutes,
				assignment.hourlyRate);
	}
}
