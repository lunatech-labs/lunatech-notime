package utils.predicates;

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
	 * A predicate that checks if the week number of a {@link HourEntry} is
	 * equal to the provided week number
	 * 
	 * @param weekNumber
	 *            Week number to which the {@link HourEntry}'s week number must
	 *            be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalWeekNumber(final int weekNumber) {
		return new EqualWeekNumber(weekNumber);
	}

	/**
	 * A predicate that checks if the week number of a {@link HourEntry} is
	 * equal to the provided week number
	 */
	private static class EqualWeekNumber implements Predicate<HourEntry> {
		private final int weekNumber;

		private EqualWeekNumber(final int weekNumber) {
			this.weekNumber = weekNumber;
		}

		public boolean apply(final HourEntry entry) {
			return entry.date.getWeekOfWeekyear() == weekNumber;
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

}
