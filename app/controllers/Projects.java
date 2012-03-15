package controllers;

import models.Project;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.project.createProject;
import views.html.admin.project.editProject;
import views.html.admin.project.projects;

public class Projects extends Controller{
	
	@Transactional(readOnly=true)
	public static Result all() {
		return ok(projects.render(Project.findAll()));
	}
	
	@Transactional(readOnly=true)
	public static Result add() {
		Form<Project> newForm = form(Project.class);
		return ok(createProject.render(newForm));
	}
	
	@Transactional
	public static Result create() {
		Form<Project> filledForm = form(Project.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(createProject.render(filledForm));
		
		Project projectToBeCreated = filledForm.get();
		
		if(Project.hasDuplicity(projectToBeCreated)) {
			flash("error", Project.validateDuplicity(projectToBeCreated));
			return badRequest(createProject.render(filledForm));
		}
		
		projectToBeCreated.save();
		return redirect(routes.Projects.all());
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long id) {
		Form<Project> filledForm = form(Project.class).fill(Project.findById(id));
		return ok(editProject.render(id, filledForm));
	}
	
	@Transactional
	public static Result update(Long id) {
		Form<Project> filledForm = form(Project.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(editProject.render(id, filledForm));
		
		Project projectToBeUpdated = filledForm.get();
		
		if(Project.hasDuplicity(id, projectToBeUpdated)) {
			flash("error", Project.validateDuplicity(id, projectToBeUpdated));
			return badRequest(editProject.render(id, filledForm));
		}
		
		projectToBeUpdated.update(id);
		return redirect(routes.Projects.all());
	}
	
	@Transactional
	public static Result delete(Long id) {
		Project.findById(id).delete();
		return redirect(routes.Projects.all());
	}

}
