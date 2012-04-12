package util;

import java.math.BigDecimal;

public class CalculationUtil {

	/**
	 * Because the hours and minutes are saved apart, minutes can become more
	 * than 59 when adding some entries. This method calculates the total hours
	 * and the remaining minutes and formats them.
	 * 
	 * @param hours
	 *            The hours
	 * @param minutes
	 *            The minutes, can be more than 59
	 * @return A string of the total hours and the remaining minutes, in the
	 *         format: 5h30m
	 */
	public static String formatTotalHoursMinutes(long hours, long minutes) {
		hours += minutes / 60;
		minutes = minutes % 60;
		return hours + "h " + minutes + "m";
	}

	/**
	 * Because the hours and minutes are saved apart, minutes can become more
	 * than 59 when adding some entries. This method calculates the total hours
	 * and the remaining minutes and formats them.
	 * 
	 * @param hours
	 *            The hours
	 * @param minutes
	 *            The minutes, can be more than 59
	 * @return A string of the total hours and the remaining minutes, in the
	 *         format: 5h30m
	 */
	public static String formatTotalHoursMinutes(int hours, int minutes) {
		return formatTotalHoursMinutes((long) hours, (long) minutes);
	}

	/**
	 * Calculates the total turnover by multiplying the hours with the hourly
	 * rate and the minutes with the hourly rate
	 * 
	 * @param hours
	 *            The hours on which a turnover needs to be calculated
	 * @param minutes
	 *            The minutes on which a turnover needs to be calculated
	 * @param hourlyRate
	 *            The hourly rate of the hours and minutes
	 * @return A BigDecimal with 2 decimals
	 */
	public static BigDecimal totalTurnover(long hours, long minutes,
			BigDecimal hourlyRate) {
		BigDecimal hoursCasted = new BigDecimal(hours);
		BigDecimal minutesToHours = new BigDecimal((double) minutes / 60);
		BigDecimal totalTurnover = new BigDecimal(0);
		hourlyRate = hourlyRate == null ? new BigDecimal(0) : hourlyRate;
		totalTurnover = hourlyRate.multiply(hoursCasted);
		totalTurnover = totalTurnover.add(hourlyRate.multiply(minutesToHours));
		return totalTurnover.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
