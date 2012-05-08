package controllers;

import java.util.Collections;
import java.util.List;

import models.Project;
import models.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.project.createProject;
import views.html.admin.project.editProject;
import views.html.admin.project.projects;
import be.objectify.deadbolt.actions.And;
import be.objectify.deadbolt.actions.Restrictions;

public class Projects extends Controller {

	@Transactional(readOnly = true)
	@Restrictions({ @And("admin"), @And("customerManager") })
	public static Result all() {
		List<Project> projectsList = Collections.emptyList();
		final User user = Application.getCurrentUser();

		if (user.isAdmin()) {
			projectsList = Project.findAll();
		} else {
			projectsList = Project.findAllForCustomerManager(user);
		}
		return ok(projects.render(projectsList));
	}

	@Transactional(readOnly = true)
	@Restrictions({ @And("admin"), @And("customerManager") })
	public static Result add() {
		Form<Project> newForm = form(Project.class);
		return ok(createProject.render(newForm));
	}

	@Transactional
	@Restrictions({ @And("admin"), @And("customerManager") })
	public static Result create() {
		Form<Project> filledForm = form(Project.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createProject.render(filledForm));

		Project project = filledForm.get();
		final User user = Application.getCurrentUser();

		// Check is customer manager is allowed to do this
		if (!user.isAdmin() && user.isCustomerManager()) {
			if (!project.customer.customerManagers.contains(user)) {
				filledForm.reject(new ValidationError("customer.id", Messages
						.get("validation.notCustomerManager"), null));
				return badRequest(createProject.render(filledForm));
			}
		}

		project.save();
		return redirect(routes.Projects.all());
	}

	@Transactional(readOnly = true)
	@Restrictions({ @And("admin"), @And("customerManager") })
	public static Result edit(Long id) {
		final User user = Application.getCurrentUser();
		final Project project = Project.findById(id);

		// Check is customer manager is allowed to do this
		if (!user.isAdmin() && user.isCustomerManager()) {
			if (!project.customer.customerManagers.contains(user)) {
				flash("error", Messages.get("project.notCustomerManager"));
				return redirect(routes.Projects.all());
			}
		}

		Form<Project> filledForm = form(Project.class).fill(project);
		return ok(editProject.render(id, filledForm));
	}

	@Transactional
	@Restrictions({ @And("admin"), @And("customerManager") })
	public static Result update(Long id) {
		Form<Project> filledForm = form(Project.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editProject.render(id, filledForm));

		Project project = filledForm.get();
		final User user = Application.getCurrentUser();

		// Check is customer manager is allowed to do this
		if (!user.isAdmin() && user.isCustomerManager()) {
			if (!project.customer.customerManagers.contains(user)) {
				filledForm.reject(new ValidationError("customer.id", Messages
						.get("project.notCustomerManager"), null));
				return badRequest(editProject.render(id, filledForm));
			}
		}

		project.update(id);
		return redirect(routes.Projects.all());
	}

	@Transactional
	@Restrictions({ @And("admin"), @And("customerManager") })
	public static Result delete(Long id) {
		final User user = Application.getCurrentUser();
		final Project project = Project.findById(id);

		// Check is customer manager is allowed to do this
		if (!user.isAdmin() && user.isCustomerManager()) {
			if (!project.customer.customerManagers.contains(user)) {
				flash("error", Messages.get("project.notCustomerManager"));
				return redirect(routes.Projects.all());
			}
		}

		if (!Project.findById(id).delete()) {
			flash("error", Messages.get("project.notDeletable"));
		}

		return redirect(routes.Projects.all());
	}

}
