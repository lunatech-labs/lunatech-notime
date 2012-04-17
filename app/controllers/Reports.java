package controllers;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.reports.overviewPerWeek;
import beans.ReportOptions;
import datastructures.reports.Report;

public class Reports extends Controller {

	@Transactional(readOnly = true)
	public static Result overviewPerWeek() {
		final Report report;
		final Form<ReportOptions> optionsForm = form(ReportOptions.class)
				.bindFromRequest();

		if (optionsForm.data().isEmpty()) { // New form
			final Form<ReportOptions> newForm = form(ReportOptions.class);
			report = Report.getEmptyReport();
			return ok(overviewPerWeek.render(newForm, report));
		} else if (optionsForm.hasErrors()) { // Submitted form with errors
			report = Report.getEmptyReport();
			return badRequest(overviewPerWeek.render(optionsForm, report));
		} else { // Submitted form without errors
			final ReportOptions options = optionsForm.get();
			report = Report.getOverviewPerWeek(options.getAllProjects(),
					options.beginDate, options.endDate);
			return ok(overviewPerWeek.render(optionsForm, report));
		}
	}

}
