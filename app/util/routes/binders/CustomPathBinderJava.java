package util.routes.binders;

import org.joda.time.DateTime;

import play.api.mvc.PathBindable;
import scala.Either;
import scala.Right;

public class CustomPathBinderJava implements PathBindable<DateTime> {

	@Override
	public Either<String, DateTime> bind(String key, String txt) {
		return new Right(new DateTime());
	}

	@Override
	public String unbind(String arg0, DateTime arg1) {
		return null;
	}

	@Override
	public String javascriptUnbind() {
		return null;
	}

}