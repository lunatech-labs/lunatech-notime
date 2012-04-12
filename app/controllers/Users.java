package controllers;

import models.HourEntry;
import models.User;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.user.createUser;
import views.html.admin.user.editUser;
import views.html.admin.user.users;
import views.html.user.assignments;
import views.html.user.daysTooFewHours;

@Security.Authenticated(Secured.class)
public class Users extends Controller {
	
	@Transactional(readOnly=true)
	public static Result all() {
		return ok(users.render(User.findAll()));
	}
	
	@Transactional(readOnly = true)
	public static Result read(Long id) {
		return ok(views.html.user.user.render(User.findById(id)));
	}

	public static Result add() {
		Form<User> newForm = form(User.class);
		return ok(createUser.render(newForm));
	}
	
	@Transactional
	public static Result create() {
		Form<User> filledForm = form(User.class).bindFromRequest();
				
		if(filledForm.hasErrors()) 
			return badRequest(createUser.render(filledForm));

		User userToBeCreated = filledForm.get();
		
		if(User.hasDuplicity(userToBeCreated)) {
			flash("error", User.validateDuplicity(userToBeCreated));
			return badRequest(createUser.render(filledForm));
		}

		userToBeCreated.save();
		return redirect(routes.Users.all());
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long userId) {
		Form<User> filledForm = form(User.class).fill(User.findById(userId));
		return ok(editUser.render(userId, filledForm));
	}
	
	@Transactional
	public static Result update(Long userId) {
		Form<User> filledForm = form(User.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(editUser.render(userId, filledForm));

		User userToBeUpdated = filledForm.get();
		
		if(User.hasDuplicity(userId, userToBeUpdated)) {
			flash("error", User.validateDuplicity(userId, userToBeUpdated));
			return badRequest(editUser.render(userId, filledForm));
		}

		userToBeUpdated.update(userId);
		return redirect(routes.Users.all());
	}
	
	@Transactional
	public static Result delete(Long userId) {
		User.findById(userId).delete();
		return redirect(routes.Users.all());
	}
	
	@Transactional(readOnly=true)
	public static Result assignmentsOverview(Long userId) {
		return ok(assignments.render(userId, User.findById(userId).assignments));
	}

	@Transactional(readOnly = true)
	public static Result daysTooFewHours(Long userId) {
		return ok(daysTooFewHours.render(userId, HourEntry.findWithTooFewHoursForUser(userId)));
	}

}
