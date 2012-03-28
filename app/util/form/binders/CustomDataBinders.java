package util.form.binders;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;

import play.data.format.Formatters;
import util.DateTimeUtil;

public class CustomDataBinders {

	public void register() {
		registerDateTimeFormatter();
	}

	private void registerDateTimeFormatter() {
		Formatters.register(DateTime.class,
				new Formatters.SimpleFormatter<DateTime>() {


					@Override
					public DateTime parse(String input, Locale l)
							throws ParseException {
						return DateTimeUtil.parseDate(input);
					}

					@Override
					public String print(DateTime input, Locale l) {
						return DateTimeUtil.formatDate(input);
					}

				});
	}

}
