package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Users extends Controller {
	
	public static Result all() {
		return ok(views.html.user.users.render(User.all()));
	}

	public static Result add() {
		Form<User> newForm = form(User.class);
		return ok(views.html.user.createUser.render(newForm));
	}
	
	public static Result create() {
		Form<User> filledForm = form(User.class).bindFromRequest();
		User userToBeCreated = filledForm.get();
		
		if(filledForm.hasErrors()) 
			return badRequest(views.html.user.createUser.render(filledForm));

		if(User.hasDuplicity(userToBeCreated)) {
			flash("error", User.validateDuplicity(userToBeCreated));
			return badRequest(views.html.user.createUser.render(filledForm));
		}

		User.create(userToBeCreated);
		return redirect(routes.Users.all());
	}
	
	public static Result edit(Long id) {
		Form<User> filledForm = form(User.class).fill(User.read(id));
		return ok(views.html.user.editUser.render(id, filledForm));
	}
	
	public static Result update(Long id) {
		Form<User> filledForm = form(User.class).bindFromRequest();
		User userToBeUpdated = filledForm.get();
		
		if(filledForm.hasErrors())
			return badRequest(views.html.user.editUser.render(id, filledForm));

		if(User.hasDuplicity(id, userToBeUpdated)) {
			flash("error", User.validateDuplicity(id, userToBeUpdated));
			return badRequest(views.html.user.editUser.render(id, filledForm));
		}

		User.update(id, userToBeUpdated);
		return redirect(routes.Users.all());
	}
	
	public static Result delete(Long id) {
		User.delete(id);
		return redirect(routes.Users.all());
	}

}
