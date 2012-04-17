package utils;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {

	// Suppress default constructor for noninstantiability
	private DateUtil() {

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
	public static boolean between(LocalDate dateToCheck, LocalDate beginDate,
			LocalDate endDate) {
		return dateToCheck.isAfter(beginDate) && dateToCheck.isBefore(endDate);
	}

	/**
	 * Get the current week number
	 * 
	 * @return An int with as value the current week number
	 */
	public static int currentWeekNumber() {
		return new LocalDate().getWeekOfWeekyear();
	}

	/**
	 * Get the first date of a week
	 * 
	 * @param date
	 *            A date in a week of which the first date is returned
	 * @return A new LocalDate object with as value the first date of a week
	 */
	public static LocalDate firstDateOfWeek(LocalDate date) {
		return date.dayOfWeek().withMinimumValue();
	}

	/**
	 * Get the last date of a week
	 * 
	 * @param date
	 *            A date in a week of which the last date is returned
	 * @return A new LocalDate object with as value the last date of a week
	 */
	public static LocalDate lastDateOfWeek(LocalDate date) {
		return date.dayOfWeek().withMaximumValue();
	}

	/**
	 * Get the first date of a month
	 * 
	 * @param date
	 *            A date in a month of which the first date is returned
	 * @return A new LocalDate object with as value the first date of a month
	 */
	public static LocalDate firstDateOfMonth(LocalDate date) {
		return date.dayOfMonth().withMinimumValue();
	}

	/**
	 * Get the last date of a month
	 * 
	 * @param date
	 *            A date in a month of which the last date is returned
	 * @return A new LocalDate object with as value the last date of a month
	 */
	public static LocalDate lastDateOfMonth(LocalDate date) {
		return date.dayOfMonth().withMaximumValue();
	}

	/**
	 * Formats the date to a String. Format type dd-MM-yyyy
	 * 
	 * @param date
	 *            The LocalDate object to be formatted
	 * @return A String with the formatted date
	 */
	public static String formatDate(LocalDate date) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		return date.toString(fmt);
	}

	/**
	 * Formats the date to a String
	 * 
	 * @param date
	 *            The LocalDate object to be formatted
	 * @param pattern
	 *            The format pattern
	 * @return A String with the formatted date
	 */
	public static String formatDate(LocalDate date, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return date.toString(fmt);
	}

	/**
	 * Parses the String to a LocalDate object. Format type dd-MM-yyy
	 * 
	 * @param date
	 *            The String to be parsed
	 * @return A LocalDate object
	 * @throws ParseException
	 *             Thrown when the parsing failed
	 */
	public static LocalDate parseDate(String date) throws ParseException {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		return fmt.parseLocalDate(date);
	}

	/**
	 * Checks if 2 LocalDates are on the same date, without the time portion
	 * 
	 * @param firstDate
	 *            A {@link LocalDate}
	 * @param secondDate
	 *            A {@link LocalDate}
	 * @return true if the two dates are the same
	 */
	public static boolean isSameDate(LocalDate firstDate, LocalDate secondDate) {
		return firstDate.compareTo(secondDate) == 0;
	}

	/**
	 * Get the weeknumbers between 2 dates
	 * 
	 * @param beginDate
	 *            The date of the first week
	 * @param endDate
	 *            The date of the last week (this weeknumber is also included)
	 * @return A List of Integers of the weeknumbers
	 */
	public static List<Integer> getWeekNumbers(final LocalDate beginDate,
			final LocalDate endDate) {
		final List<Integer> weekNumbers = new LinkedList<Integer>();
		LocalDate indexDate = firstDateOfWeek(beginDate);
		while (indexDate.isBefore(lastDateOfWeek(endDate))) {
			weekNumbers.add(indexDate.getWeekOfWeekyear());
			indexDate = indexDate.plusWeeks(1);
		}
		return weekNumbers;
	}

}
