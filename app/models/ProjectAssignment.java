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

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import util.DateTimeUtil;

@Entity
public class ProjectAssignment {

	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public Project project;

	@ManyToOne
	public User user;

	@Formats.DateTime(pattern = "dd-MM-yy")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;

	@Formats.DateTime(pattern = "dd-MM-yy")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime endDate;

	@Column(scale = 2)
	public BigDecimal hourlyRate = new BigDecimal(0);

	/**
	 * Inserts this project assignment
	 * 
	 * @param projectId
	 *            The id of the related project
	 */
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Sets the project, maximizes the time of the endDate and inserts this
	 * project assignment
	 * 
	 * @param projectId
	 *            The id of the related project
	 */
	public void saveAndMaximizeTime(Long projectId) {
		this.project = Project.findById(projectId);
		this.endDate = DateTimeUtil.maximizeTimeOfDate(this.endDate);
		JPA.em().persist(this);
	}

	/**
	 * Sets the project, maximizes the time of the endDate and updates this
	 * project assignment
	 * 
	 * @param assignmentId
	 *            The id of the project assignment that is going to be updated
	 * @param projectId
	 *            The id of the related project
	 */
	public void update(Long assignmentId, Long projectId) {
		this.id = assignmentId;
		this.project = Project.findById(projectId);
		this.endDate = DateTimeUtil.maximizeTimeOfDate(this.endDate);
		JPA.em().merge(this);
	}

	/**
	 * Deletes this project assignment
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	/**
	 * Find a project assignment by id
	 * 
	 * @param assignmentId
	 *            The id of the project assignment to be searched for
	 * @return A project assignment
	 */
	public static ProjectAssignment findById(Long assignmentId) {
		return JPA.em().find(ProjectAssignment.class, assignmentId);
	}

	/**
	 * Find all project assignments for a user
	 * 
	 * @param userId
	 *            The id of the user which project assignments are to be
	 *            searched for
	 * @return A List of project assignments
	 */
	public static List<ProjectAssignment> findAllForUser(Long userId) {
		return JPA
				.em()
				.createQuery(
						"from ProjectAssignment pa "
								+ "where pa.user.id = :userId "
								+ "order by pa.id desc")
				.setParameter("userId", userId).getResultList();
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
	public static List<ProjectAssignment> findByUserAndProject(User user,
			Project project) {
		return JPA
				.em()
				.createQuery(
						"from ProjectAssignment pa "
								+ "where pa.user.id = :userId "
								+ "and pa.project.id = :projectId "
								+ "order by pa.id desc")
				.setParameter("userId", user.id)
				.setParameter("projectId", project.id).getResultList();
	}

	/**
	 * All assignments for a user
	 * 
	 * @return A Map with as key the assignment id and as value the project name
	 */
	public static Map<String, String> optionsFor(Long userId) {
		LinkedHashMap<String, String> assignments = new LinkedHashMap<String, String>();
		assignments.put("", "");

		for (ProjectAssignment assignment : findAllForUser(userId)) {
			if (ProjectAssignment.isDateInAssignmentRange(new DateTime(),
					assignment.id))
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
	public static void assignAllUsersTo(Project project) {
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
	public static void assignAllDefaultProjectsTo(User user) {
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
	public static boolean isExistingAssignment(User user, Project project) {
		return !findByUserAndProject(user, project).isEmpty();
	}

	// VALIDATION METHODS NEED TO BE REPLACED BY ANNOTATIONS OR BE REWRITTEN
	public static boolean hasValidDates(ProjectAssignment assignment) {
		return validateDates(assignment).isEmpty();
	}

	public static String validateDates(ProjectAssignment assignment) {
		if (assignment.startDate.toLocalDate().compareTo(
				assignment.endDate.toLocalDate()) > 0)
			return "Start date is after the End date";
		else
			return new String();
	}

	public static boolean isDateInAssignmentRange(DateTime date,
			Long assignmentId) {
		ProjectAssignment assignment = ProjectAssignment.findById(assignmentId);
		return DateTimeUtil.between(date, assignment.startDate,
				assignment.endDate);
	}
}
