package controllers;

import models.Project;
import models.ProjectAssignment;
import models.User;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.project.assignment.createProjectAssignment;
import views.html.admin.project.assignment.editProjectAssignment;
import be.objectify.deadbolt.actions.And;
import be.objectify.deadbolt.actions.Restrictions;
import be.objectify.deadbolt.actions.Unrestricted;

@Security.Authenticated(Secured.class)
@Restrictions({ @And("admin"), @And("customerManager"), @And("projectManager") })
public class ProjectAssignments extends Controller {

	@Transactional(readOnly = true)
	public static Result add(Long projectId) {
		Form<ProjectAssignment> newForm = form(ProjectAssignment.class);
		return ok(createProjectAssignment.render(projectId, newForm));
	}

	@Transactional
	public static Result create(Long projectId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class)
				.bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createProjectAssignment.render(projectId,
					filledForm));

		final Project project = Project.findById(projectId);
		final User user = Application.getCurrentUser();
		if (!user.hasAdminRole()) {
			// Check if customer manager is allowed to do this
			if (user.hasCustomerManagerRole()) {
				if (!project.customer.customerManagers.contains(user)) {
					filledForm.reject(Messages
							.get("project.notCustomerManager"));
					return badRequest(createProjectAssignment.render(projectId,
							filledForm));
				}
			}
			// Check if project manager is allowed to do this
			if (user.hasProjectManagerRole()) {
				if (!project.projectManager.equals(user)) {
					filledForm
							.reject(Messages.get("project.notProjectManager"));
					return badRequest(createProjectAssignment.render(projectId,
							filledForm));
				}
			}
		}

		if (!project.active) {
			filledForm.reject("Project is not active!");
			return badRequest(createProjectAssignment.render(projectId,
					filledForm));
		}

		filledForm.get().save(projectId);
		return redirect(routes.Projects.all());
	}

	@Transactional(readOnly = true)
	public static Result edit(Long projectId, Long assignmentId) {
		final Project project = Project.findById(projectId);
		final User user = Application.getCurrentUser();
		if (!user.hasAdminRole()) {
			// Check if customer manager is allowed to do this
			if (user.hasCustomerManagerRole()) {
				if (!project.customer.customerManagers.contains(user)) {
					flash("error", Messages.get("project.notCustomerManager"));
					return redirect(routes.Projects.all());
				}
			}
			// Check if project manager is allowed to do this
			if (user.hasProjectManagerRole()) {
				if (!project.projectManager.equals(user)) {
					flash("error", Messages.get("project.notProjectManager"));
					return redirect(routes.Projects.all());
				}
			}
		}

		Form<ProjectAssignment> newForm = form(ProjectAssignment.class).fill(
				ProjectAssignment.findById(assignmentId));
		return ok(editProjectAssignment
				.render(projectId, assignmentId, newForm));
	}

	@Transactional
	public static Result update(Long projectId, Long assignmentId) {
		Form<ProjectAssignment> filledForm = form(ProjectAssignment.class)
				.bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editProjectAssignment.render(projectId,
					assignmentId, filledForm));

		final Project project = Project.findById(projectId);
		final User user = Application.getCurrentUser();
		if (!user.hasAdminRole()) {
			// Check if customer manager is allowed to do this
			if (user.hasCustomerManagerRole()) {
				if (!project.customer.customerManagers.contains(user)) {
					filledForm.reject(Messages
							.get("project.notCustomerManager"));
					return badRequest(editProjectAssignment.render(projectId,
							assignmentId, filledForm));
				}
			}
			// Check if project manager is allowed to do this
			if (user.hasProjectManagerRole()) {
				if (!project.projectManager.equals(user)) {
					filledForm
							.reject(Messages.get("project.notProjectManager"));
					return badRequest(editProjectAssignment.render(projectId,
							assignmentId, filledForm));
				}
			}
		}

		if (!project.active) {
			filledForm.reject("Project is not active!");
			return badRequest(createProjectAssignment.render(projectId,
					filledForm));
		}

		filledForm.get().update(assignmentId, projectId);
		return redirect(routes.Projects.all());
	}

	@Transactional
	public static Result delete(Long assignmentId) {
		final ProjectAssignment assignment = ProjectAssignment
				.findById(assignmentId);
		final Project project = assignment.project;
		final User user = Application.getCurrentUser();
		if (!user.hasAdminRole()) {
			// Check if customer manager is allowed to do this
			if (user.hasCustomerManagerRole()) {
				if (!project.customer.customerManagers.contains(user)) {
					flash("error", Messages.get("project.notCustomerManager"));
					return redirect(routes.Projects.all());
				}
			}
			// Check if project manager is allowed to do this
			if (user.hasProjectManagerRole()) {
				if (!project.projectManager.equals(user)) {
					flash("error", Messages.get("project.notProjectManager"));
					return redirect(routes.Projects.all());
				}
			}
		}

		if (!assignment.delete()) {
			flash("error", Messages.get("assignment.notDeletable"));
		}
		return redirect(routes.Projects.all());
	}

	@Transactional
	@Unrestricted
	public static Result toggleStarred(Long userId, Long assignmentId) {
		ProjectAssignment assignment = ProjectAssignment.findById(assignmentId);
		if (assignment.starred) {
			assignment.starred = false;
		} else {
			assignment.starred = true;
		}
		assignment.update(assignmentId);
		return redirect(routes.Users.assignmentsOverview(userId));
	}

}
