package controllers;

import beans.LoginForm;
import models.User;
import play.Routes;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.login;
import views.html.accountOptions;
import views.html.adminHome;

public class Application extends Controller {

	public static Result index() {
		return redirect(routes.Application.login());
	}

	@Transactional(readOnly = true)
	public static Result login() {
		return ok(login.render(form(LoginForm.class)));
	}

	@Transactional(readOnly = true)
	public static Result authenticate() {
		Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		} else {
			String username = loginForm.get().username;
			session("username", username);
			return redirect(routes.Users.home());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result logout() {
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Application.login());
	}

	@Security.Authenticated(Secured.class)
	@Transactional(readOnly = true)
	public static Result admin() {
		return ok(adminHome.render());
	}

	@Security.Authenticated(Secured.class)
	@Transactional(readOnly = true)
	public static Result accountOptions() {
		return ok(accountOptions.render());
	}

	/**
	 * Get the user that is currently logged in
	 * 
	 * @return A {@link User}
	 */
	@Security.Authenticated(Secured.class)
	public static User getCurrentUser() {
		return User.findByUsername(session("username"));
	}

	@Security.Authenticated(Secured.class)
	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter("jsRoutes",
				controllers.routes.javascript.HourEntries.addForDay()));
	}

}