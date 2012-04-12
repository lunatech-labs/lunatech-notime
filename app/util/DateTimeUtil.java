package util;

import java.text.ParseException;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {

	/**
	 * Set the time of a date to 0:0:0:000
	 * 
	 * @param date
	 *            The date on which the time needs to be minimized
	 * @return A new DateTime object with the same date and the minimized time
	 */
	public static DateTime minimizeTimeOfDate(DateTime date) {
		return new DateTime(date.getYear(), date.getMonthOfYear(),
				date.getDayOfMonth(), 0, 0, 0, 0);
	}

	/**
	 * Set the time of a date to 23:59:59:999
	 * 
	 * @param date
	 *            The date on which the time needs to be maximized
	 * @return A new DateTime object with the same date and the maximized time
	 */
	public static DateTime maximizeTimeOfDate(DateTime date) {
		return new DateTime(date.getYear(), date.getMonthOfYear(),
				date.getDayOfMonth(), 23, 59, 59, 999);
	}

	/**
	 * Checks if a date is in a range
	 * 
	 * @param dateToCheck
	 *            The date that has to be in the range
	 * @param beginDate
	 *            The begin date of the range
	 * @param endDate
	 *            The end date of the range
	 * @return A boolean which is true if the date is in the range
	 */
	public static boolean between(DateTime dateToCheck, DateTime beginDate,
			DateTime endDate) {
		Interval interval = new Interval(beginDate, endDate);
		return interval.contains(dateToCheck);
	}

	/**
	 * Get the current week number
	 * 
	 * @return An int with as value the current week number
	 */
	public static int currentWeekNumber() {
		return new DateTime().getWeekOfWeekyear();
	}

	/**
	 * Get the first date of a week
	 * 
	 * @param date
	 *            A date in a week of which the first date is returned
	 * @return A new DateTime object with as value the first date of a week
	 */
	public static DateTime firstDateOfWeek(DateTime date) {
		return minimizeTimeOfDate(date.dayOfWeek().withMinimumValue());
	}

	/**
	 * Get the last date of a week
	 * 
	 * @param date
	 *            A date in a week of which the last date is returned
	 * @return A new DateTime object with as value the last date of a week
	 */
	public static DateTime lastDateOfWeek(DateTime date) {
		return maximizeTimeOfDate(date.dayOfWeek().withMaximumValue());
	}

	/**
	 * Get the first date of a month
	 * 
	 * @param date
	 *            A date in a month of which the first date is returned
	 * @return A new DateTime object with as value the first date of a month
	 */
	public static DateTime firstDateOfMonth(DateTime date) {
		return minimizeTimeOfDate(date.dayOfMonth().withMinimumValue());
	}

	/**
	 * Get the last date of a month
	 * 
	 * @param date
	 *            A date in a month of which the last date is returned
	 * @return A new DateTime object with as value the last date of a month
	 */
	public static DateTime lastDateOfMonth(DateTime date) {
		return maximizeTimeOfDate(date.dayOfMonth().withMaximumValue());
	}

	/**
	 * Formats the date to a String. Format type dd-MM-yyyy
	 * 
	 * @param date
	 *            The DateTime object to be formatted
	 * @return A String with the formatted date
	 */
	public static String formatDate(DateTime date) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		return date.toString(fmt);
	}

	/**
	 * Formats the date to a String
	 * 
	 * @param date
	 *            The DateTime object to be formatted
	 * @param pattern
	 *            The format pattern
	 * @return A String with the formatted date
	 */
	public static String formatDate(DateTime date, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return date.toString(fmt);
	}

	/**
	 * Parses the String to a DateTime object. Format type dd-MM-yyy
	 * 
	 * @param date
	 *            The String to be parsed
	 * @return A DateTime object
	 * @throws ParseException
	 *             Thrown when the parsing failed
	 */
	public static DateTime parseDate(String date) throws ParseException {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		return fmt.parseDateTime(date);
	}

	/**
	 * Checks if 2 DateTimes are on the same date, without the time portion
	 *
	 * @param firstDate
	 *            A {@link DateTime}
	 * @param secondDate
	 *            A {@link DateTime}
	 * @return true if the two dates are the same
	 */
	public static boolean isSameDate(DateTime firstDate, DateTime secondDate) {
		LocalDate firstLocalDate = firstDate.toLocalDate();
		LocalDate secondLocalDate = secondDate.toLocalDate();
		return firstLocalDate.compareTo(secondLocalDate) == 0;
	}

}
