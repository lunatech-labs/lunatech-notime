package utils.predicates;

import models.Customer;
import models.Project;

import com.google.common.base.Predicate;

public class ProjectPredicates {

	/**
	 * A predicate that checks if the customer of a {@link Project} is equal to
	 * the given customer
	 * 
	 * @param customer
	 *            Customer to which the {@link Project}'s customer must be equal
	 * @return A {@link Predicate}
	 */
	public static Predicate<Project> equalCustomer(final Customer customer) {
		return new EqualCustomer(customer);
	}

	/**
	 * Checks if the customer of a {@link Project} is equal to
	 * the given customer
	 */
	private static class EqualCustomer implements Predicate<Project> {
		private final Customer customer;

		private EqualCustomer(final Customer customer) {
			this.customer = customer;
		}

		public boolean apply(final Project project) {
			return project.customer == customer;
		}
	}

}
