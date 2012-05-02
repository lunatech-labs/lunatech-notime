package security;

import play.mvc.Http.Context;
import be.objectify.deadbolt.DeadboltHandler;
import be.objectify.deadbolt.DynamicResourceHandler;

public class DynamicResourceHandlerImpl implements DynamicResourceHandler {

	@Override
	public boolean isAllowed(String name, String meta,
			DeadboltHandler deadboltHandler, Context context) {
		return false;
	}

	@Override
	public boolean checkPermission(String permissionValue,
			DeadboltHandler deadboltHandler, Context ctx) {
		return false;
	}

}
