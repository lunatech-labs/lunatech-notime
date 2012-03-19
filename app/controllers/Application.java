package controllers;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import util.datastructures.Login;
import views.html.index;
import views.html.login;
import views.html.admin.overview;

public class Application extends Controller {

	public static Result login() {
		return ok(login.render(form(Login.class)));
	}

	@Transactional(readOnly=true)
	public static Result authenticate() {
		Form<Login> loginForm = form(Login.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		} else {
			session("username", loginForm.get().username);
			return redirect(routes.Projects.all());
		}
	}

	public static Result logout() {
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Application.login());
	}

	public static Result index() {
		return ok(index.render());
	}

	public static Result admin() {
		return ok(overview.render());
	}

}