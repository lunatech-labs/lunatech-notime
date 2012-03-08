package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.joda.time.DateTime;
import org.hibernate.annotations.Type;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import util.DateUtil;

@Entity
@SequenceGenerator(name = "projectassignment_seq", sequenceName = "projectassignment_seq")
public class ProjectAssignment {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectassignment_seq")
	public Long id;
	
	@ManyToOne
	public Project project;
	
	@ManyToOne
	public User user;
	
	@Required
	@Formats.DateTime(pattern="dd-MM-yy")
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;
	
	@Required
	@Formats.DateTime(pattern="dd-MM-yy")
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime endDate;
	
	@Required
	@Column(scale=2)
	public BigDecimal hourlyRate;
	
	public static void create(ProjectAssignment assignment, Long projectId) {
		assignment.project = Project.read(projectId);
		assignment.endDate = DateUtil.maximizeTimeOfDate(assignment.endDate);
		JPA.em().persist(assignment);
	}
	
	public static ProjectAssignment read(Long id) {
		return JPA.em().find(ProjectAssignment.class, id);	
	}
	
	public static void update(Long assignmentId, Long projectId, ProjectAssignment assignmentToBeUpdated) {
		ProjectAssignment assignment = read(assignmentId);
		assignment.project = Project.read(projectId);
		assignment.user = assignmentToBeUpdated.user;
		assignment.startDate = assignmentToBeUpdated.startDate;
		assignment.endDate = DateUtil.maximizeTimeOfDate(assignmentToBeUpdated.endDate);
		assignment.hourlyRate = assignmentToBeUpdated.hourlyRate;
		JPA.em().merge(assignment);
	}
	
	public static void delete(Long id) {
		JPA.em().remove(ProjectAssignment.read(id));
	}
	
	public static boolean hasValidDates(ProjectAssignment assignment) {
		return validateDates(assignment).isEmpty();
	}
	
	public static String validateDates(ProjectAssignment assignment) {
		if (assignment.startDate.toLocalDate().compareTo(assignment.endDate.toLocalDate()) > 0)
			return "Start date is after the End date";
		else
			return new String();		
	}
	
	public static boolean isDateInAssignmentRange(DateTime date, Long assignmentId) {
		ProjectAssignment assignment = ProjectAssignment.read(assignmentId);
		return DateUtil.between(date, assignment.startDate, assignment.endDate);
	}
}
