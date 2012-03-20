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

	@Required
	@Formats.DateTime(pattern = "dd-MM-yy")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;

	@Required
	@Formats.DateTime(pattern = "dd-MM-yy")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime endDate;

	@Required
	@Column(scale = 2)
	public BigDecimal hourlyRate;

	/**
	 * Sets the project, maximizes the time of the endDate and inserts this
	 * project assignment
	 * 
	 * @param projectId
	 *            The id of the related project
	 */
	public void save(Long projectId) {
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
	 * Find all project assignment for a user
	 * 
	 * @param userId
	 *            The id of the user which project assignments are to be searched for
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
