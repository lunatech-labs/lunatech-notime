package util;

import models.ProjectAssignment;

import org.joda.time.DateTime;

import com.google.common.base.Predicate;

import formbeans.WeekHourEntryBean;

public class Predicates {

	/**
	 * A predicate that checks if the assignment and the date of a
	 * {@link WeekHourEntryBean} is equal to the given assignment and date
	 * 
	 * @param assignment
	 *            Assignment to which the {@link WeekHourEntryBean} must be
	 *            equal to
	 * @param day
	 *            Day to which the {@link WeekHourEntryBean} must be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<WeekHourEntryBean> equalAssignmentAndDay(
			final ProjectAssignment assignment, final DateTime day) {
		return new EqualAssignmentAndDay(assignment, day);
	}

	/**
	 * Checks if the assignment and the date of a {@link WeekHourEntryBean} is
	 * equal to the given assignment and date
	 */
	public static class EqualAssignmentAndDay implements
			Predicate<WeekHourEntryBean> {
		private final ProjectAssignment assignment;
		private final DateTime day;

		private EqualAssignmentAndDay(final ProjectAssignment assignment,
				final DateTime day) {
			this.assignment = assignment;
			this.day = day;
		}

		public boolean apply(final WeekHourEntryBean entry) {
			return entry.assignment == assignment
					&& DateTimeUtil.isSameDate(entry.date, day);
		}
	}

}
