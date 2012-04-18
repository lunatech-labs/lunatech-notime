package utils.predicates;

import org.joda.time.LocalDate;

import utils.DateUtil;

import models.HourEntry;
import models.Project;
import models.User;

import com.google.common.base.Predicate;

public class HourEntryPredicates {

	/**
	 * A predicate that checks if the user of a {@link HourEntry} is equal to
	 * the provided user
	 * 
	 * @param user
	 *            User to which the {@link HourEntry}'s user must be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalUser(final User user) {
		return new EqualUser(user);
	}

	/**
	 * A predicate that checks if the user of a {@link HourEntry} is equal to
	 * the provided user
	 */
	private static class EqualUser implements Predicate<HourEntry> {
		private final User user;

		private EqualUser(final User user) {
			this.user = user;
		}

		public boolean apply(final HourEntry entry) {
			return entry.assignment.user == user;
		}
	}

	/**
	 * A predicate that checks if the project and the week number of a
	 * {@link HourEntry} are equal to the provided project and week number
	 * 
	 * @param project
	 *            Project to which the {@link HourEntry}'s project must be equal
	 *            to
	 * @param weekNumber
	 *            Week number to which the {@link HourEntry}'s week number must
	 *            be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalProjectAndWeekNumber(
			final Project project, final int weekNumber) {
		return new EqualProjectAndWeekNumber(project, weekNumber);
	}

	/**
	 * A predicate that checks if the project and the week number of a
	 * {@link HourEntry} are equal to the provided project and week number
	 */
	private static class EqualProjectAndWeekNumber implements
			Predicate<HourEntry> {
		private final Project project;
		private final int weekNumber;

		private EqualProjectAndWeekNumber(final Project project,
				final int weekNumber) {
			this.project = project;
			this.weekNumber = weekNumber;
		}

		public boolean apply(final HourEntry entry) {
			return entry.assignment.project == project
					&& entry.date.getWeekOfWeekyear() == weekNumber;
		}
	}

	/**
	 * A predicate that checks if the user of a {@link HourEntry} is equal to
	 * the provided user and if the {@link HourEntry}'s date is between the
	 * provided dates
	 * 
	 * @param user
	 *            User to which the {@link HourEntry}'s user must be equal to
	 * @param beginDate
	 *            The date from which entries are filtered
	 * @param endDate
	 *            The date till which entries are filtered
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalUserAndBetweenDates(
			final User user, final LocalDate beginDate, final LocalDate endDate) {
		return new EqualUserAndBetweenDates(user, beginDate, endDate);
	}

	/**
	 * A predicate that checks if the user of a {@link HourEntry} is equal to
	 * the provided user and if the {@link HourEntry}'s date is between the
	 * provided dates
	 */
	private static class EqualUserAndBetweenDates implements
			Predicate<HourEntry> {
		private final User user;
		private final LocalDate beginDate;
		private final LocalDate endDate;

		private EqualUserAndBetweenDates(final User user,
				final LocalDate beginDate, final LocalDate endDate) {
			this.user = user;
			this.beginDate = beginDate;
			this.endDate = endDate;
		}

		public boolean apply(final HourEntry entry) {
			return entry.assignment.user == user
					&& DateUtil.between(entry.date, beginDate, endDate);
		}
	}

}
