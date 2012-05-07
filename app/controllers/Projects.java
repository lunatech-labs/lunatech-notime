package controllers;

import java.util.Collections;
import java.util.List;

import models.Project;
import models.User;
import models.security.UserRole;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.project.createProject;
import views.html.admin.project.editProject;
import views.html.admin.project.projects;
import be.objectify.deadbolt.actions.And;
import be.objectify.deadbolt.actions.Restrictions;

public class Projects extends Controller {

	@Transactional(readOnly = true)
	@Restrictions({@And("admin"), @And("customerManager")})
	public static Result all() {
		List<Project> projectsList = Collections.emptyList();
		final User user = User.findByUsername(session("username"));

		if (user.containsRole(UserRole.adminRole())) {
			projectsList = Project.findAll();
		} else {
			projectsList = Project.findAllForCustomerManager(user);
		}

		return ok(projects.render(projectsList));
	}

	@Transactional(readOnly = true)
	@Restrictions({@And("admin"), @And("customerManager")})
	public static Result add() {
		Form<Project> newForm = form(Project.class);
		return ok(createProject.render(newForm));
	}

	@Transactional
	@Restrictions({@And("admin"), @And("customerManager")})
	public static Result create() {
		Form<Project> filledForm = form(Project.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createProject.render(filledForm));

		filledForm.get().save();
		return redirect(routes.Projects.all());
	}

	@Transactional(readOnly = true)
	@Restrictions({@And("admin"), @And("customerManager")})
	public static Result edit(Long id) {
		Form<Project> filledForm = form(Project.class).fill(
				Project.findById(id));
		return ok(editProject.render(id, filledForm));
	}

	@Transactional
	@Restrictions({@And("admin"), @And("customerManager")})
	public static Result update(Long id) {
		Form<Project> filledForm = form(Project.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editProject.render(id, filledForm));

		filledForm.get().update(id);		
		return redirect(routes.Projects.all());
	}

	@Transactional
	@Restrictions({@And("admin"), @And("customerManager")})
	public static Result delete(Long id) {
		if (!Project.findById(id).delete()) {
			flash("error", "The project could not be deleted. Probably one of its assignments is not deletable.");
		}
		return redirect(routes.Projects.all());
	}

}
