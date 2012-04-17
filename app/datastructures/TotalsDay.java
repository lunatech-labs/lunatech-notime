package datastructures;

import org.joda.time.LocalDate;

import utils.CalculationUtil;

/**
 * Datastructure for displaying the totals for a day
 */
public class TotalsDay {

	public LocalDate date;

	public long hours;

	public long minutes;

	public String hoursMinutes;

	/**
	 * Constructor for {@link TotalsDay}. Also fills the hoursMinutes
	 * field with a formatted String of the hours and minutes
	 * 
	 * @param date
	 *            The day for which the totals are calculated
	 * @param hours
	 *            The total hours booked on this assignment
	 * @param minutes
	 *            The total minutes booked on this minutes
	 */
	public TotalsDay(LocalDate date, Long hours,
			Long minutes) {
		this.date = date;
		this.minutes = minutes;
		this.hours = hours;
		hoursMinutes = CalculationUtil.formatTotalHoursMinutes(hours,
				minutes);
	}

	
}
