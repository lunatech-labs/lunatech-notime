package controllers;

import models.Project;
import models.ProjectAssignment;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.project.assignment.createProjectAssignment;
import views.html.admin.project.assignment.editProjectAssignment;

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

		if (!Project.findById(projectId).active) {
			filledForm.reject("Project is not active!");
			return badRequest(createProjectAssignment.render(projectId,
					filledForm));
		}

		filledForm.get().save(projectId);
		return redirect(routes.Projects.all());
	}

	@Transactional(readOnly = true)
	public static Result edit(Long projectId, Long assignmentId) {
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

		if (!Project.findById(projectId).active) {
			filledForm.reject("Project is not active!");
			return badRequest(createProjectAssignment.render(projectId,
					filledForm));
		}

		filledForm.get().update(assignmentId, projectId);
		return redirect(routes.Projects.all());
	}

	@Transactional
	public static Result delete(Long assignmentId) {
		if (!ProjectAssignment.findById(assignmentId).delete()) {
			flash("error",
					"The assignment could not be deleted. Probably there are still hours booked on this assignment.");
		}
		return redirect(routes.Projects.all());
	}

	@Transactional
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
