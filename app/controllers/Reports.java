package controllers;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.reports.entriesPerCustomerPerProjectPerWeek;
import views.html.reports.assignmentTotalsPerEmployee;
import views.html.reports.entriesPerWeek;
import beans.ReportOptions;
import datastructures.reports.Report;

public class Reports extends Controller {

	@Transactional(readOnly = true)
	public static Result entriesPerCustomerPerProjectPerWeek() {
		final Report report;
		final Form<ReportOptions> optionsForm = form(ReportOptions.class)
				.bindFromRequest();

		if (optionsForm.data().isEmpty()) { // New form
			final Form<ReportOptions> newForm = form(ReportOptions.class);
			report = Report.getEmptyReport();
			return ok(entriesPerCustomerPerProjectPerWeek.render(newForm,
					report));
		} else if (optionsForm.hasErrors()) { // Submitted form with errors
			report = Report.getEmptyReport();
			return badRequest(entriesPerCustomerPerProjectPerWeek.render(
					optionsForm, report));
		} else { // Submitted form without errors
			final ReportOptions options = optionsForm.get();
			report = Report.getReportEntriesPerCustomerPerProjectPerWeek(
					options.getAllProjects(), options.beginDate,
					options.endDate);
			return ok(entriesPerCustomerPerProjectPerWeek.render(optionsForm,
					report));
		}
	}

	@Transactional(readOnly = true)
	public static Result assignmentTotalsPerUser() {
		final Report report;
		final Form<ReportOptions> optionsForm = form(ReportOptions.class)
				.bindFromRequest();

		if (optionsForm.data().isEmpty()) { // New form
			final Form<ReportOptions> newForm = form(ReportOptions.class);
			report = Report.getEmptyReport();
			return ok(assignmentTotalsPerEmployee.render(newForm, report));
		} else if (optionsForm.hasErrors()) { // Submitted form with errors
			report = Report.getEmptyReport();
			return badRequest(assignmentTotalsPerEmployee.render(optionsForm,
					report));
		} else { // Submitted form without errors
			final ReportOptions options = optionsForm.get();
			report = Report.getReportAssignmentTotalsPerUser(
					options.getAllUsers(), options.beginDate, options.endDate);
			return ok(assignmentTotalsPerEmployee.render(optionsForm, report));
		}
	}

	@Transactional(readOnly = true)
	public static Result entriesPerWeek() {
		final Report report;
		final Form<ReportOptions> optionsForm = form(ReportOptions.class)
				.bindFromRequest();

		if (optionsForm.data().isEmpty()) { // New form
			final Form<ReportOptions> newForm = form(ReportOptions.class);
			report = Report.getEmptyReport();
			return ok(entriesPerWeek.render(newForm, report));
		} else if (optionsForm.hasErrors()) { // Submitted form with errors
			report = Report.getEmptyReport();
			return badRequest(entriesPerWeek.render(optionsForm, report));
		} else { // Submitted form without errors
			final ReportOptions options = optionsForm.get();
			report = Report.getReportEntriesPerWeek(options.getAllProjects(),
					options.beginDate, options.endDate);
			return ok(entriesPerWeek.render(optionsForm, report));
		}
	}

}
