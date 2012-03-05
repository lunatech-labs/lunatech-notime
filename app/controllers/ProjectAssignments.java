package controllers;

import models.ProjectAssignment;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.project.assignment.createProjectAssignment;
import views.html.admin.project.assignment.editProjectAssignment;

public class ProjectAssignments extends Controller {
	
	@Transactional(readOnly=true)
	public static Result add(Long projectId) {
		Form<ProjectAssignment> newForm = form(ProjectAssignment.class);
		return ok(createProjectAssignment.render(projectId, newForm));
	}
	
	@Transactional
	public static Result create(Long projectId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class).bindFromRequest();
		
		if(filledForm.hasErrors())
			return badRequest(createProjectAssignment.render(projectId, filledForm));
		
		ProjectAssignment assignment = filledForm.get();
		
		if(!ProjectAssignment.hasValidDates(assignment)) {
			flash("error", ProjectAssignment.validateDates(assignment));
			return badRequest(createProjectAssignment.render(projectId, filledForm)); 
		}		
		
		ProjectAssignment.create(assignment, projectId);
		return redirect(routes.Projects.all());
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> newForm = form(ProjectAssignment.class).fill(ProjectAssignment.read(assignmentId));
		return ok(editProjectAssignment.render(projectId, assignmentId, newForm));
	}
	
	@Transactional
	public static Result update(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(editProjectAssignment.render(projectId, assignmentId, filledForm));
		
		ProjectAssignment assignment = filledForm.get();
		
		if(!ProjectAssignment.hasValidDates(assignment)) {
			flash("error", ProjectAssignment.validateDates(assignment));
			return badRequest(editProjectAssignment.render(projectId, assignmentId, filledForm)); 
		}
		
		ProjectAssignment.update(assignmentId, projectId, assignment);
		return redirect(routes.Projects.all());
	}
	
	@Transactional
	public static Result delete(Long projectAssignmentId) {
		ProjectAssignment.delete(projectAssignmentId);
		return redirect(routes.Projects.all());
	}

}
