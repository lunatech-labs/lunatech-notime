package controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import models.Tag;
import static play.libs.Json.toJson;

public class Tags extends Controller {

	@Transactional(readOnly = true)
	public static Result find() {
		String term = request().queryString().get("term")[0];
		return ok(toJson(Tag.findTagsWhichContain(term)));
	}

}
