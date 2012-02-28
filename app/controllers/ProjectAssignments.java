package controllers;

import models.ProjectAssignment;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class ProjectAssignments extends Controller {
	
	public static Result add(Long projectId) {
		Form<ProjectAssignment> newForm = form(ProjectAssignment.class);
		return ok(views.html.project.assignment.createProjectAssignment.render(projectId, newForm));
	}
	
	public static Result create(Long projectId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class).bindFromRequest();
		
		if (filledForm.hasErrors())
			return badRequest(views.html.project.assignment.createProjectAssignment.render(projectId, filledForm));
		
		ProjectAssignment assignment = filledForm.get();
		
		if (!ProjectAssignment.validDates(assignment)) {
			flash("error", ProjectAssignment.validateDates(assignment));
			return badRequest(views.html.project.assignment.createProjectAssignment.render(projectId, filledForm)); 
		}		
		
		ProjectAssignment.create(assignment, projectId);
		return redirect(routes.Projects.all());
	}
	
	public static Result edit(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> newForm = form(ProjectAssignment.class).fill(ProjectAssignment.read(assignmentId));
		return ok(views.html.project.assignment.editProjectAssignment.render(projectId, assignmentId, newForm));
	}
	
	public static Result update(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class).bindFromRequest();
				
		if (filledForm.hasErrors())
			return badRequest(views.html.project.assignment.editProjectAssignment.render(projectId, assignmentId, filledForm));
		
		ProjectAssignment assignment = filledForm.get();
		
		if (!ProjectAssignment.validDates(assignment)) {
			flash("error", ProjectAssignment.validateDates(assignment));
			return badRequest(views.html.project.assignment.editProjectAssignment.render(projectId, assignmentId, filledForm)); 
		}
		
		System.out.println(assignment.hourlyRate);
		
		ProjectAssignment.update(assignmentId, projectId, assignment);
		return redirect(routes.Projects.all());
	}
	
	public static Result delete(Long projectAssignmentId) {
		ProjectAssignment.delete(projectAssignmentId);
		return redirect(routes.Projects.all());
	}

}
