package utils.predicates;

import models.ProjectAssignment;

import org.joda.time.LocalDate;

import utils.DateUtil;
import beans.HourEntryInWeek;

import com.google.common.base.Predicate;

public class HourEntryInWeekPredicates {

	/**
	 * A predicate that checks if the assignment and the date of a
	 * {@link HourEntryInWeek} are equal to the given assignment and date
	 * 
	 * @param assignment
	 *            Assignment to which the {@link HourEntryInWeek}'s assignment must be equal to
	 * @param day
	 *            Day to which the {@link HourEntryInWeek}'s date must be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntryInWeek> equalAssignmentAndDay(
			final ProjectAssignment assignment, final LocalDate day) {
		return new EqualAssignmentAndDay(assignment, day);
	}

	/**
	 * Checks if the assignment and the date of a {@link HourEntryInWeek} is equal
	 * to the given assignment and date
	 */
	private static class EqualAssignmentAndDay implements
			Predicate<HourEntryInWeek> {
		private final ProjectAssignment assignment;
		private final LocalDate day;

		private EqualAssignmentAndDay(final ProjectAssignment assignment,
				final LocalDate day) {
			this.assignment = assignment;
			this.day = day;
		}

		public boolean apply(final HourEntryInWeek entry) {
			return entry.assignment == assignment
					&& DateUtil.isSameDate(entry.date, day);
		}
	}

}
