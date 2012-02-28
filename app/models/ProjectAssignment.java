package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class ProjectAssignment extends Model{

	@Id
	public Long id;
	
	@ManyToOne
	public Project project;
	
	@ManyToOne
	public User user;
	
	@Required
	@Formats.DateTime(pattern="dd-MM-yy")
	public Date startDate;
	
	@Required
	@Formats.DateTime(pattern="dd-MM-yy")
	public Date endDate;
	
	@Required
	@Column(scale=2)
	public BigDecimal hourlyRate;
	
	public static Finder<Long, ProjectAssignment> find = new Finder<Long, ProjectAssignment>(Long.class, ProjectAssignment.class);

	public static void create(ProjectAssignment assignment, Long projectId) {
		assignment.project = Project.read(projectId);
		assignment.save();
	}
	
	public static ProjectAssignment read(Long id) {
		return find.byId(id);		
	}
	
	public static void update(Long assignmentId, Long projectId, ProjectAssignment assignmentToBeUpdated) {
		ProjectAssignment assignment = read(assignmentId);
		assignment.project = Project.read(projectId);
		assignment.user = assignmentToBeUpdated.user;
		assignment.startDate = assignmentToBeUpdated.startDate;
		assignment.endDate = assignmentToBeUpdated.endDate;
		assignment.hourlyRate = assignmentToBeUpdated.hourlyRate;
		System.out.println(assignment.hourlyRate);
		assignment.update();
	}
	
	public static void delete(Long id) {
		read(id).delete();
	}
	
	public static boolean validDates(ProjectAssignment assignment) {
		return validateDates(assignment).isEmpty();
	}
	
	public static String validateDates(ProjectAssignment assignment) {
		if (assignment.startDate.after(assignment.endDate))
			return "Start date is after the End date";
		else
			return new String();		
	}
}
