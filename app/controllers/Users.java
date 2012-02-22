package controllers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;

public class Users extends Controller {
	
	public static Result all() {
		return ok(views.html.users.render(User.all()));
	}

	public static Result add() {
		Form<User> newForm = form(User.class);
		return ok(views.html.createUser.render(newForm));
	}
	
	public static Result create() {
		Form<User> filledForm = form(User.class).bindFromRequest();
		
		if(filledForm.hasErrors()) 
			return badRequest(views.html.createUser.render(filledForm));

		if(User.hasDuplicity(filledForm.get())) {
			flash("error", User.validateDuplicity(filledForm.get()));
			return badRequest(views.html.createUser.render(filledForm));
		}

		User.create(filledForm.get());
		return redirect(routes.Users.all());
	}
	
	public static Result edit(Long id) {
		Form<User> filledForm = form(User.class).fill(User.read(id));
		return ok(views.html.editUser.render(id, filledForm));
	}
	
	public static Result update(Long id) {
		Form<User> filledForm = form(User.class).bindFromRequest();
		
		if(filledForm.hasErrors())
			return badRequest(views.html.editUser.render(id, filledForm));

		if(User.hasDuplicity(id, filledForm.get())) {
			flash("error", User.validateDuplicity(id, filledForm.get()));
			return badRequest(views.html.editUser.render(id, filledForm));
		}

		User.update(id, filledForm.get());
		return redirect(routes.Users.all());
	}
	
	public static Result delete(Long id) {
		User.delete(id);
		return redirect(routes.Users.all());
	}

}
