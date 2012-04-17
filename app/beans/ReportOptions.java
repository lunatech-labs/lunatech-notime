package beans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Customer;
import models.Project;

import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;

public class ReportOptions {

	public List<Customer> customers;

	public List<Project> projects;

	@Required
	public LocalDate beginDate;

	@Required
	public LocalDate endDate;

	/**
	 * Finds all projects. So selects all the projects from the selected
	 * customers and add the selected projects
	 * 
	 * @return A Set of {@link Project}s
	 */
	public Set<Project> getAllProjects() {
		final Set<Project> projects = new HashSet<Project>();
		if (customers != null && !customers.isEmpty()) {
			for (Customer customer : customers) {
				projects.addAll(Project.findAllForCustomer(customer));
			}
		}
		if (this.projects != null) {
			projects.addAll(this.projects);
		}
		return projects;
	}
}
