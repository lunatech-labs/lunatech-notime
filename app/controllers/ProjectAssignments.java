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
		
		ProjectAssignment assignmentToBeCreated = filledForm.get();
		
		if(!ProjectAssignment.hasValidDates(assignmentToBeCreated)) {
			flash("error", ProjectAssignment.validateDates(assignmentToBeCreated));
			return badRequest(createProjectAssignment.render(projectId, filledForm)); 
		}		
		
		assignmentToBeCreated.saveAndMaximizeTime(projectId);
		return redirect(routes.Projects.all());
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> newForm = form(ProjectAssignment.class).fill(ProjectAssignment.findById(assignmentId));
		return ok(editProjectAssignment.render(projectId, assignmentId, newForm));
	}
	
	@Transactional
	public static Result update(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(editProjectAssignment.render(projectId, assignmentId, filledForm));
		
		ProjectAssignment assignmentToBeUpdated = filledForm.get();
		
		if(!ProjectAssignment.hasValidDates(assignmentToBeUpdated)) {
			flash("error", ProjectAssignment.validateDates(assignmentToBeUpdated));
			return badRequest(editProjectAssignment.render(projectId, assignmentId, filledForm)); 
		}
		
		assignmentToBeUpdated.update(assignmentId, projectId);
		return redirect(routes.Projects.all());
	}
	
	@Transactional
	public static Result delete(Long assignmentId) {
		ProjectAssignment.findById(assignmentId).delete();
		return redirect(routes.Projects.all());
	}

}
