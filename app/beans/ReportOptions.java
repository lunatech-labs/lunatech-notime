package beans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Customer;
import models.Project;
import models.User;

import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;

public class ReportOptions {

	public List<Customer> customers;

	public List<Project> projects;

	public List<User> users;

	@Required
	public LocalDate beginDate;

	@Required
	public LocalDate endDate;

	/**
	 * Finds all projects. Gets all the projects from the selected customers and
	 * add the selected projects. If there are no customers and projects
	 * selected, all projects will be returned.
	 * 
	 * @return A Set of {@link Project}s
	 */
	public Set<Project> getAllProjects() {
		final Set<Project> projects = new HashSet<Project>();
		if (customers == null && this.projects == null) {
			projects.addAll(Project.findAll());
		} else {
			if (customers != null && !customers.isEmpty()) {
				for (Customer customer : customers) {
					projects.addAll(Project.findAllForCustomer(customer));
				}
			}
			if (this.projects != null) {
				projects.addAll(this.projects);
			}
		}
		return projects;
	}

	/**
	 * Finds all users. Gets all the users from the selected projects and adds
	 * the selected users
	 * 
	 * @return A Set of {@link User}s
	 */
	public Set<User> getAllUsers() {
		final Set<User> users = new HashSet<User>();
		if (projects != null && !projects.isEmpty()) {
			for (Project project : projects) {
				users.addAll(User.findAll(project));
			}
		}
		if (this.users != null) {
			users.addAll(this.users);
		}
		return users;
	}
}
