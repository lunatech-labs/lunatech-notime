package controllers;

import models.Project;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Projects extends Controller{
	
	public static Result all() {
		return ok(views.html.project.projects.render(Project.all()));
	}
	
	public static Result add() {
		Form<Project> newForm = form(Project.class);
		return ok(views.html.project.createProject.render(newForm));
	}
	
	public static Result create() {
		Form<Project> filledForm = form(Project.class).bindFromRequest();
		Project projectToBeCreated = filledForm.get();
		
		if(filledForm.hasErrors())
			return badRequest(views.html.project.createProject.render(filledForm));
		
		if(Project.hasDuplicity(projectToBeCreated)) {
			flash("error", Project.validateDuplicity(projectToBeCreated));
			return badRequest(views.html.project.createProject.render(filledForm));
		}
		
		Project.create(projectToBeCreated);
		return redirect(routes.Projects.all());
	}
	
	public static Result edit(Long id) {
		Form<Project> filledForm = form(Project.class).fill(Project.read(id));
		return ok(views.html.project.editProject.render(id, filledForm));
	}
	
	public static Result update(Long id) {
		Form<Project> filledForm = form(Project.class).bindFromRequest();
		Project projectToBeUpdated = filledForm.get();
		
		if(filledForm.hasErrors())
			return badRequest(views.html.project.editProject.render(id, filledForm));
		
		if(Project.hasDuplicity(id, projectToBeUpdated)) {
			flash("error", Project.validateDuplicity(id, projectToBeUpdated));
			return badRequest(views.html.project.editProject.render(id, filledForm));
		}
		
		Project.update(id, projectToBeUpdated);
		return redirect(routes.Projects.all());
	}
	
	public static Result delete(Long id) {
		Project.delete(id);
		return redirect(routes.Projects.all());
	}

}
