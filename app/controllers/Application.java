package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.admin.overview;

public class Application extends Controller {
  
  public static Result index() {
    return ok(index.render());
  }
  
  public static Result admin() {
	  return ok(overview.render());
  }
  
}