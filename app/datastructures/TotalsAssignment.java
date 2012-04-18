package datastructures;

import java.math.BigDecimal;

import utils.CalculationUtil;

import models.ProjectAssignment;

/**
 * Datastructure for displaying the totals for an assignment
 */
public class TotalsAssignment {

	public ProjectAssignment assignment;

	public long hours;

	public long minutes;

	public String hoursMinutes;

	public BigDecimal turnover;

	/**
	 * Constructor for {@link TotalsAssignment}. It also fills the hoursMinutes
	 * field with a formatted String of the hours and minutes, and calculates
	 * the turnover field
	 * 
	 * @param assignment
	 *            The assignment on which the totals are calculated
	 * @param hours
	 *            The total hours booked on this assignment
	 * @param minutes
	 *            The total minutes booked on this minutes
	 */
	public TotalsAssignment(ProjectAssignment assignment, Long hours,
			Long minutes) {
		this.assignment = assignment;
		this.minutes = minutes;
		this.hours = hours;
		hoursMinutes = CalculationUtil.formatTotalHoursMinutes(hours, minutes);
		turnover = CalculationUtil.totalTurnover(hours, minutes,
				assignment.hourlyRate);
	}

	/**
	 * Add hours and minutes to this object. It also updates the hoursMinutes
	 * field with a formatted String of the hours and minutes, and recalculates
	 * the turnover field
	 * 
	 * @param hours
	 *            The amount of hours that need to be added
	 * @param minutes
	 *            The amount of minutes that need to be added
	 */
	public void addHoursMinutes(long hours, long minutes) {
		this.hours += hours;
		this.minutes += minutes;
		hoursMinutes = CalculationUtil.formatTotalHoursMinutes(this.hours,
				this.minutes);
		turnover = CalculationUtil.totalTurnover(this.hours, this.minutes,
				assignment.hourlyRate);
	}
}
