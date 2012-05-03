package security;

import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import be.objectify.deadbolt.AbstractDeadboltHandler;
import be.objectify.deadbolt.DynamicResourceHandler;
import be.objectify.deadbolt.models.RoleHolder;

public class DeadboltHandlerImpl extends AbstractDeadboltHandler {

	@Override
	public Result beforeRoleCheck(Context context) {
		return null;
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(Context context) {
		return new DynamicResourceHandlerImpl();
	}

	@Override
	public RoleHolder getRoleHolder(Context context) {
		return User.findByUsername(context.session().get("username"));
	}

	@Override
	public Result onAccessFailure(Context context, String content) {
		return forbidden(views.html.forbidden.render());
	}

}