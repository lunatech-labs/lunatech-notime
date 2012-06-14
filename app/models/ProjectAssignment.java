package models;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import utils.DateUtil;

@Entity
public class ProjectAssignment implements Comparable<ProjectAssignment> {

	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public Project project;

	@ManyToOne
	public User user;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
	public LocalDate beginDate;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
	public LocalDate endDate;

	@Column(scale = 2)
	public BigDecimal hourlyRate = new BigDecimal(0);

	public boolean starred;

	public boolean active;

	/**
	 * Inserts this project assignment
	 */
	public void save() {
		active = true;
		JPA.em().persist(this);
	}

	/**
	 * Inserts this project assignment
	 * 
	 * @param projectId
	 *            The id of the related project
	 */
	public void save(long projectId) {
		this.project = Project.findById(projectId);
		active = true;
		JPA.em().persist(this);
	}

	/**
	 * Sets the project because the form doesn't have a project field and
	 * updates this project assignment
	 * 
	 * @param assignmentId
	 *            The id of the project assignment that is going to be updated
	 * @param projectId
	 *            The id of the related project
	 */
	public void update(Long assignmentId, Long projectId) {
		this.id = assignmentId;
		this.project = Project.findById(projectId);
		JPA.em().merge(this);
	}

	/**
	 * Updates this project assignment
	 * 
	 * @param assignmentId
	 *            The id of the project assignment that is going to be updated
	 */
	public void update(Long assignmentId) {
		this.id = assignmentId;
		JPA.em().merge(this);
	}

	/**
	 * Updates this project assignment
	 */
	public void update() {
		JPA.em().merge(this);
	}

	/**
	 * Deletes this project assignment
	 * 
	 * @return true if the project assignment is removed
	 */
	public boolean delete() {
		boolean deletable = isDeletable();
		if (deletable) {
			JPA.em().remove(this);
		}
		return deletable;
	}

	/**
	 * Inactivates this project assignment
	 */
	public void inactivate() {
		active = false;
		update();
	}

	/**
	 * Checks if a project assignment is deletable. A assignment is deletable
	 * when there are no hours booked on the assignment
	 * 
	 * @return true if the assignment is deletable
	 */
	public boolean isDeletable() {
		return HourEntry.findAllForAssignment(this).isEmpty();
	}

	/**
	 * Find a project assignment by id
	 * 
	 * @param assignmentId
	 *            The id of the project assignment to be searched for
	 * @return A project assignment
	 */
	public static ProjectAssignment findById(final Long assignmentId) {
		return JPA.em().find(ProjectAssignment.class, assignmentId);
	}

	/**
	 * Find all active project assignments for a user
	 * 
	 * @param user
	 *            The user which project assignments are to be
	 *            searched for
	 * @return A List of project assignments
	 */
	public static List<ProjectAssignment> findAllActiveForUser(final User user) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<ProjectAssignment> query = cb
				.createQuery(ProjectAssignment.class);
		Root<ProjectAssignment> assignment = query
				.from(ProjectAssignment.class);
		query.where(cb.equal(assignment.get(ProjectAssignment_.user), user),
				cb.isTrue(assignment.get(ProjectAssignment_.active)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all active project assignments for a project
	 * 
	 * @param projectId
	 *            The id of the project which project assignments are to be
	 *            searched for
	 * @return A List of project assignments
	 */
	public static List<ProjectAssignment> findAllActiveForProject(final Long projectId) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<ProjectAssignment> query = cb
				.createQuery(ProjectAssignment.class);
		Root<ProjectAssignment> assignment = query
				.from(ProjectAssignment.class);

		Join<ProjectAssignment, Project> project = assignment
				.join(ProjectAssignment_.project);

		query.where(cb.equal(project.get(Project_.id), projectId),
				cb.isTrue(assignment.get(ProjectAssignment_.active)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all starred project assignments for a user
	 * 
	 * @param user
	 *            The user which starred project assignments are to be
	 *            searched for
	 * @return A List of project assignments
	 */
	public static List<ProjectAssignment> findAllStarredForUser(final User user) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<ProjectAssignment> query = cb
				.createQuery(ProjectAssignment.class);
		Root<ProjectAssignment> assignment = query
				.from(ProjectAssignment.class);

		query.where(cb.equal(assignment.get(ProjectAssignment_.user), user),
				cb.equal(assignment.get(ProjectAssignment_.starred), true));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all project assignments for a user on a project
	 * 
	 * @param user
	 *            The user which project assignments are to be searched for
	 * @param project
	 *            The project which project assignments are to be searched for
	 * @return A List of project assignments
	 */
	public static List<ProjectAssignment> findByUserAndProject(final User user,
			final Project project) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<ProjectAssignment> query = cb
				.createQuery(ProjectAssignment.class);
		Root<ProjectAssignment> assignment = query
				.from(ProjectAssignment.class);
		query.where(cb.equal(assignment.get(ProjectAssignment_.user), user),
				cb.equal(assignment.get(ProjectAssignment_.project), project));
		query.orderBy(cb.desc(assignment.get(ProjectAssignment_.id)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * All active assignments for a user
	 * 
	 * @return A Map with as key the assignment id and as value the project name
	 */
	public static Map<String, String> optionsFor(final User user) {
		final LinkedHashMap<String, String> assignments = new LinkedHashMap<String, String>();

		for (ProjectAssignment assignment : findAllActiveForUser(user)) {
			assignments.put(assignment.id.toString(),
					assignment.project.name.toString());
		}
		return assignments;
	}

	/**
	 * Assigns all users to a project
	 * 
	 * @param project
	 *            The project all users are assigned to
	 */
	public static void assignAllUsersTo(final Project project) {
		List<User> users = User.findAll();
		for (User user : users) {
			if (!isExistingAssignment(user, project)) {
				ProjectAssignment pa = new ProjectAssignment();
				pa.user = user;
				pa.project = project;
				pa.save();
			}
		}
	}

	/**
	 * Assigns all default projects to a user
	 * 
	 * @param user
	 *            The user all default projects are assigned to
	 */
	public static void assignAllDefaultProjectsTo(final User user) {
		List<Project> defaultProjects = Project.findAllDefaultProjects();
		for (Project defaultProject : defaultProjects) {
			ProjectAssignment pa = new ProjectAssignment();
			pa.user = user;
			pa.project = defaultProject;
			pa.save();
		}
	}

	/**
	 * Checks if a user is already assigned to a project
	 * 
	 * @param user
	 *            The user to be checked for
	 * @param project
	 *            The project to be checked for
	 * @return A boolean which is true if there are 1 or more assignments with
	 *         this user and project
	 */
	public static boolean isExistingAssignment(final User user, final Project project) {
		return !findByUserAndProject(user, project).isEmpty();
	}

	/**
	 * Checks if a date is between, or equal to the begin and/or end date of an assignment.
	 * If there is only a begin date, the date to check must be after the begin date.
	 * If there is only an end date, the date to check must be before the end date.
	 * If an assignment has no dates, true is returned.
	 *
	 * @param date
	 *            The date which must be in the assignment rate
	 * @param assignmentId
	 *            The id of the assingment to be validated
	 * @return A boolean which is true if the date is in the assignment range
	 */
	public static boolean isDateInAssignmentRange(final LocalDate date,	final Long assignmentId) {
		ProjectAssignment assignment = ProjectAssignment.findById(assignmentId);

		if (assignment.beginDate != null && assignment.endDate != null)
			return DateUtil.between(date, assignment.beginDate,	assignment.endDate) || date.isEqual(assignment.beginDate) || date.isEqual(assignment.endDate);

		if (assignment.beginDate != null)
			return date.isAfter(assignment.beginDate) || date.isEqual(assignment.beginDate);

		if (assignment.endDate != null)
			return date.isBefore(assignment.endDate) || date.isEqual(assignment.endDate);

		return true;
	}

	public String validate() {
		if (beginDate != null && endDate != null) {
			if (!isStartDateNotAfterEndDate()) {
				return "Start date is after the End date";
			}
		}

		if (!hasActiveUser())
			return "User is not active!";

		return null;
	}

	public boolean isStartDateNotAfterEndDate() {
		return beginDate.isBefore(endDate.plusDays(1));
	}

	public boolean hasActiveUser() {
		// Check if user is loaded
		if (user.username == null)
			user = User.findById(user.id);
		return user.active;
	}

	@Override
	public int compareTo(ProjectAssignment assignment) {
		return this.id.compareTo(assignment.id);
	}

}
