package util;

import java.math.BigDecimal;

public class CalculationUtil {
	
	// Calculates the total hours and minutes. Returns a String with the format 5h30m
	public static String totalHoursMinutesFormatter(long hours, long minutes) {
		hours += minutes / 60;
		minutes = minutes % 60;
		return hours + "h " + minutes + "m";
	}
	
	public static BigDecimal totalTurnover(long hours, long minutes, BigDecimal hourlyRate) {
		BigDecimal hoursCasted = new BigDecimal(hours);
		BigDecimal minutesToHours = new BigDecimal((double) minutes / 60);
		BigDecimal totalTurnover = new BigDecimal(0);
		totalTurnover = hourlyRate.multiply(hoursCasted);
		totalTurnover = totalTurnover.add(hourlyRate.multiply(minutesToHours));
		return totalTurnover.setScale(2, BigDecimal.ROUND_HALF_UP);
		
	}

}
