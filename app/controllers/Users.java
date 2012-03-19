package controllers;

import models.User;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.user.createUser;
import views.html.admin.user.editUser;
import views.html.admin.user.users;

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
	public static Result edit(Long id) {
		Form<User> filledForm = form(User.class).fill(User.findById(id));
		return ok(editUser.render(id, filledForm));
	}
	
	@Transactional
	public static Result update(Long id) {
		Form<User> filledForm = form(User.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(editUser.render(id, filledForm));

		User userToBeUpdated = filledForm.get();
		
		if(User.hasDuplicity(id, userToBeUpdated)) {
			flash("error", User.validateDuplicity(id, userToBeUpdated));
			return badRequest(editUser.render(id, filledForm));
		}

		userToBeUpdated.update(id);
		return redirect(routes.Users.all());
	}
	
	@Transactional
	public static Result delete(Long id) {
		User.findById(id).delete();
		return redirect(routes.Users.all());
	}

}
