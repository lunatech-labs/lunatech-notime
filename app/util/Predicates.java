package util;

import models.ProjectAssignment;

import org.joda.time.LocalDate;

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
			final ProjectAssignment assignment, final LocalDate day) {
		return new EqualAssignmentAndDay(assignment, day);
	}

	/**
	 * Checks if the assignment and the date of a {@link WeekHourEntryBean} is
	 * equal to the given assignment and date
	 */
	public static class EqualAssignmentAndDay implements
			Predicate<WeekHourEntryBean> {
		private final ProjectAssignment assignment;
		private final LocalDate day;

		private EqualAssignmentAndDay(final ProjectAssignment assignment,
				final LocalDate day) {
			this.assignment = assignment;
			this.day = day;
		}

		public boolean apply(final WeekHourEntryBean entry) {
			return entry.assignment == assignment
					&& DateUtil.isSameDate(entry.date, day);
		}
	}

}
