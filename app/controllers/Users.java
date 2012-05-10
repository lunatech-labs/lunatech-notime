package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import models.HourEntry;
import models.User;

import org.joda.time.LocalDate;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.daysTooFewHours;
import views.html.userHome;
import views.html.projectassignment.starAssignments;
import views.html.user.createUser;
import views.html.user.editUser;
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

	@Transactional(readOnly = true)
	@Unrestricted
	public static Result home() {
		return ok(userHome.render(Application.getCurrentUser()));
	}

	@Transactional(readOnly = true)
	public static Result add() {
		Form<User> newForm = form(User.class);
		return ok(createUser.render(newForm));
	}

	@Transactional
	public static Result create() {
		Form<User> filledForm = form(User.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createUser.render(filledForm));

		filledForm.get().save();
		return redirect(routes.Users.all());
	}

	@Transactional(readOnly = true)
	public static Result edit(Long userId) {
		Form<User> filledForm = form(User.class).fill(User.findById(userId));
		return ok(editUser.render(userId, filledForm));
	}

	@Transactional
	public static Result update(Long userId) {
		Form<User> filledForm = form(User.class).bindFromRequest();
		if (filledForm.hasErrors())
			return badRequest(editUser.render(userId, filledForm));

		filledForm.get().update(userId);
		return redirect(routes.Users.all());
	}

	@Transactional
	public static Result delete(Long userId) {
		if (!User.findById(userId).delete()) {
			flash("error",
					"The user could not be deleted. Probably one of its assignments is not deletable. Or the user is project manager.");
		}
		return redirect(routes.Users.all());
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
