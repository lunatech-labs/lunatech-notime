package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import beans.UserForm;

import models.HourEntry;
import models.Project;
import models.User;

import org.joda.time.LocalDate;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.daysTooFewHours;
import views.html.userHome;
import views.html.projectassignment.starAssignments;
import views.html.user.createUser;
import views.html.user.editPassword;
import views.html.user.editUser;
import views.html.user.user;
import views.html.user.users;
import views.html.user.usersForProject;
import be.objectify.deadbolt.actions.And;
import be.objectify.deadbolt.actions.Restrict;
import be.objectify.deadbolt.actions.Restrictions;
import be.objectify.deadbolt.actions.Unrestricted;
import datastructures.TotalsDay;

@Security.Authenticated(Secured.class)
@Restrict("admin")
public class Users extends Controller {

	@Transactional(readOnly = true)
	public static Result all() {
		return ok(users.render(User.findAll()));
	}

	@Transactional
	@Security.Authenticated(Secured.class)
	@Restrictions({ @And("admin"), @And("customerManager"),
			@And("projectManager"), @And("user") })
	public static Result allForProject(Long projectId) {
		List<User> users = User.findAllForProject(Project.findById(projectId));
		return ok(usersForProject.render(users));
	}

	@Transactional(readOnly = true)
	@Unrestricted
	public static Result home() {
		return ok(userHome.render(Application.getCurrentUser()));
	}

	@Transactional(readOnly = true)
	@Security.Authenticated(Secured.class)
	@Restrictions({ @And("admin"), @And("customerManager"),
			@And("projectManager"), @And("user") })
	public static Result read(Long userId) {
		return ok(user.render(User.findById(userId)));
	}

	@Transactional(readOnly = true)
	public static Result add() {
		Form<UserForm> newForm = form(UserForm.class);
		return ok(createUser.render(newForm));
	}

	@Transactional
	public static Result create() {
		Form<UserForm> filledForm = form(UserForm.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createUser.render(filledForm));

		filledForm.get().toUser().save();
		return redirect(routes.Users.all());
	}

	@Transactional(readOnly = true)
	public static Result edit(Long userId) {
		Form<UserForm> filledForm = form(UserForm.class).fill(new UserForm(User.findById(userId)));
		return ok(editUser.render(userId, filledForm));
	}

	@Transactional
	public static Result update(Long userId) {
		Form<UserForm> filledForm = form(UserForm.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editUser.render(userId, filledForm));

		filledForm.get().toUser().update(userId);
		return redirect(routes.Users.all());
	}

	@Transactional
	public static Result delete(Long userId) {
		if (!User.findById(userId).delete()) {
			flash("error", Messages.get("user.notDeletable"));
		}
		return redirect(routes.Users.all());
	}

	@Transactional(readOnly = true)
	@Security.Authenticated(Secured.class)
	@Restrictions({ @And("admin"), @And("customerManager"),
			@And("projectManager"), @And("user") })
	public static Result editPassword() {
		return ok(editPassword.render());
	}

	@Transactional
	@Security.Authenticated(Secured.class)
	@Restrictions({ @And("admin"), @And("customerManager"),
			@And("projectManager"), @And("user") })
	public static Result updatePassword() {
		final DynamicForm form = form().bindFromRequest();

		final String password = form.get("password-1");
		final String repeatedPassword = form.get("password-2");
		if (password == null || password.isEmpty() || repeatedPassword == null || repeatedPassword.isEmpty()) {
			flash("error", Messages.get("user.anEmptyPasswordField"));
			return badRequest(editPassword.render());
		}

		if (!password.equals(repeatedPassword)) {
			flash("error", Messages.get("user.passwordFieldsNotEqual"));
			return badRequest(editPassword.render());
		}

		final User user = Application.getCurrentUser();
		user.updatePassword(password);

		return redirect(routes.Users.home());
	}

	@Transactional(readOnly = true)
	@Unrestricted
	public static Result assignmentsOverview() {
		final User user = Application.getCurrentUser();
		return ok(starAssignments.render(user.assignments));
	}

	@Transactional(readOnly = true)
	@Unrestricted
	public static Result daysTooFewHours() {
		final User user = Application.getCurrentUser();
		final LocalDate beginDate = user.createdOn;
		final LocalDate endDate = new LocalDate();

		List<TotalsDay> totals = HourEntry.findTotalsForUserPerDayBetween(
				user, beginDate, endDate);
		Map<LocalDate, TotalsDay> totalsPerDay = new HashMap<LocalDate, TotalsDay>();
		for (TotalsDay total : totals) {
			totalsPerDay.put(total.date, total);
		}

		LocalDate indexDate = beginDate;
		// Also contains the days where there are no hours booked
		List<TotalsDay> totalsForAllDays = new LinkedList<TotalsDay>();
		while (indexDate.isBefore(endDate.plusDays(1))) {
			// Only weekdays
			if (indexDate.getDayOfWeek() < 6) {
				// get totals of this date
				TotalsDay indexDateTotals = totalsPerDay.get(indexDate);
				if (indexDateTotals != null) {
					if (indexDateTotals.hasEnteredTooFewHours()) {
						// if totals are not enough, the difference still need
						// to be booked
						totalsForAllDays.add(indexDateTotals);
					}
				} else {
					// if no total, there need to be booked 8 hours
					totalsForAllDays.add(new TotalsDay(indexDate, 0L, 0L));
				}
			}
			indexDate = indexDate.plusDays(1);
		}

		return ok(daysTooFewHours.render(totalsForAllDays));
	}
}
