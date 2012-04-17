package utils.predicates;

import models.HourEntry;
import models.Project;

import com.google.common.base.Predicate;

public class HourEntryPredicates {

	/**
	 * A predicate that checks if the project and the week number of a
	 * {@link HourEntry} are equal to the given project and week number
	 * 
	 * @param project
	 *            Project to which the {@link HourEntry}'s project must be equal to
	 * @param weekNumber
	 *            Week number to which the {@link HourEntry}'s week number must be equal to
	 * @return A {@link Predicate}
	 */
	public static Predicate<HourEntry> equalProjectAndWeekNumber(
			final Project project, final int weekNumber) {
		return new EqualProjectAndWeekNumber(project, weekNumber);
	}

	/**
	 * A predicate that checks if the project and the week number of a
	 * {@link HourEntry} are equal to the given project and week number
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
