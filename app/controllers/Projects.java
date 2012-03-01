package controllers;

import models.Project;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class Projects extends Controller{
	
	@Transactional(readOnly=true)
	public static Result all() {
		return ok(views.html.project.projects.render(Project.all()));
	}
	
	@Transactional(readOnly=true)
	public static Result add() {
		Form<Project> newForm = form(Project.class);
		return ok(views.html.project.createProject.render(newForm));
	}
	
	@Transactional
	public static Result create() {
		Form<Project> filledForm = form(Project.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(views.html.project.createProject.render(filledForm));
		
		Project projectToBeCreated = filledForm.get();
		
		if(Project.hasDuplicity(projectToBeCreated)) {
			flash("error", Project.validateDuplicity(projectToBeCreated));
			return badRequest(views.html.project.createProject.render(filledForm));
		}
		
		Project.create(projectToBeCreated);
		return redirect(routes.Projects.all());
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long id) {
		Form<Project> filledForm = form(Project.class).fill(Project.read(id));
		return ok(views.html.project.editProject.render(id, filledForm));
	}
	
	@Transactional
	public static Result update(Long id) {
		Form<Project> filledForm = form(Project.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(views.html.project.editProject.render(id, filledForm));
		
		Project projectToBeUpdated = filledForm.get();
		
		if(Project.hasDuplicity(id, projectToBeUpdated)) {
			flash("error", Project.validateDuplicity(id, projectToBeUpdated));
			return badRequest(views.html.project.editProject.render(id, filledForm));
		}
		
		Project.update(id, projectToBeUpdated);
		return redirect(routes.Projects.all());
	}
	
	@Transactional
	public static Result delete(Long id) {
		Project.delete(id);
		return redirect(routes.Projects.all());
	}

}
