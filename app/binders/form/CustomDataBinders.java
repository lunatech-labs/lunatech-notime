package binders.form;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.LocalDate;

import play.data.format.Formatters;
import util.DateUtil;

public class CustomDataBinders {

	public void register() {
		registerLocalDateFormatter();
	}

	private void registerLocalDateFormatter() {
		Formatters.register(LocalDate.class,
				new Formatters.SimpleFormatter<LocalDate>() {

					@Override
					public LocalDate parse(String input, Locale l)
							throws ParseException {
						return DateUtil.parseDate(input);
					}

					@Override
					public String print(LocalDate input, Locale l) {
						return DateUtil.formatDate(input);
					}

				});
	}

}
