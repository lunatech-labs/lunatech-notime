package utils.predicates;

import models.HourEntry;
import models.Project;
import models.User;

import org.joda.time.LocalDate;

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
	 * A predicate that checks if the project of a {@link HourEntry} is equal to
	 * the provided project
	 * 
	 * @param project
	 *            Project to which the {@link HourEntry}'s project must be equal
	 *            to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalProject(final Project project) {
		return new EqualProject(project);
	}

	/**
	 * A predicate that checks if the project of a {@link HourEntry} is equal to
	 * the provided project
	 */
	private static class EqualProject implements Predicate<HourEntry> {
		private final Project project;

		private EqualProject(final Project project) {
			this.project = project;
		}

		public boolean apply(final HourEntry entry) {
			return entry.assignment.project == project;
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
	 * A predicate that checks if the date of a {@link HourEntry} is equal to
	 * the provided day
	 * 
	 * @param day
	 *            Day to which the {@link HourEntry}'s date must be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalDay(final LocalDate day) {
		return new EqualDay(day);
	}

	/**
	 * A predicate that checks if the date of a {@link HourEntry} is equal to
	 * the provided day
	 */
	private static class EqualDay implements Predicate<HourEntry> {
		private final LocalDate day;

		private EqualDay(final LocalDate day) {
			this.day = day;
		}

		public boolean apply(final HourEntry entry) {
			return entry.date.equals(day);
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
	 * A predicate that checks if the project and the date of a
	 * {@link HourEntry} are equal to the provided project and day
	 * 
	 * @param project
	 *            Project to which the {@link HourEntry}'s project must be equal
	 *            to
	 * @param day
	 *            Date to which the {@link HourEntry}'s date must be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalProjectAndDay(
			final Project project, final LocalDate day) {
		return new EqualProjectAndDay(project, day);
	}

	/**
	 * A predicate that checks if the project and the date of a
	 * {@link HourEntry} are equal to the provided project and day
	 */
	private static class EqualProjectAndDay implements Predicate<HourEntry> {
		private final Project project;
		private LocalDate day;

		private EqualProjectAndDay(final Project project, final LocalDate day) {
			this.project = project;
			this.day = day;
		}

		public boolean apply(final HourEntry entry) {
			return entry.assignment.project == project
					&& entry.date.equals(day);
		}
	}

	/**
	 * A predicate that checks if the user and the week number of a
	 * {@link HourEntry} are equal to the provided user and week number
	 * 
	 * @param user
	 *            User to which the {@link HourEntry}'s user must be equal to
	 * @param weekNumber
	 *            Week number to which the {@link HourEntry}'s week number must
	 *            be equal to
	 * @return A {@link Predicate}
	 * @param weekNumber
	 * @return
	 */
	public static Predicate<HourEntry> equalUserAndWeekNumber(final User user,
			final int weekNumber) {
		return new EqualUserAndWeekNumber(user, weekNumber);
	}

	/**
	 * A predicate that checks if the user and the week number of a
	 * {@link HourEntry} are equal to the provided user and week number
	 */
	private static class EqualUserAndWeekNumber implements Predicate<HourEntry> {
		private final User user;
		private final int weekNumber;

		private EqualUserAndWeekNumber(final User user, final int weekNumber) {
			this.user = user;
			this.weekNumber = weekNumber;
		}

		public boolean apply(final HourEntry entry) {
			return entry.assignment.user == user
					&& entry.date.getWeekOfWeekyear() == weekNumber;
		}
	}

}
