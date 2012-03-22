package controllers;

import models.User;
import play.Routes;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import util.datastructures.Login;
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
			String username = loginForm.get().username;
			session("username", username);
			return redirect(routes.Users.read(User.findByUsername(username).id));
		}
	}

	public static Result logout() {
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Application.login());
	}

	public static Result index() {
		return redirect(routes.Application.login());
	}

	public static Result admin() {
		return ok(overview.render());
	}
	
	// Javascript routing    
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
            Routes.javascriptRouter("jsRoutes",            
                controllers.routes.javascript.HourEntries.addForDay()                
            )
        );
    }
    
}