package util.form.binders;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.format.Formatters;

public class CustomDataBinders {

	public void register() {
		registerDateTimeFormatter();
	}

	private void registerDateTimeFormatter() {
		Formatters.register(DateTime.class,
				new Formatters.SimpleFormatter<DateTime>() {

					DateTimeFormatter fmt = DateTimeFormat
							.forPattern("dd-MM-yyyy");

					@Override
					public DateTime parse(String input, Locale l)
							throws ParseException {
						return fmt.parseDateTime(input);
					}

					@Override
					public String print(DateTime input, Locale l) {
						return input.toString(fmt);
					}

				});
	}

}
