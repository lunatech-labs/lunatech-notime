package controllers;

import java.util.List;

import models.HourEntry;
import models.User;

import org.joda.time.LocalDate;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtil;
import views.html.hourentry.createHourEntries;
import views.html.hourentry.createHourEntriesForWeek;
import views.html.hourentry.createHourEntry;
import views.html.hourentry.createHourEntryForDay;
import views.html.hourentry.editHourEntry;
import views.html.hourentry.editHourEntryForProjectManager;
import views.html.hourentry.hourEntriesCalendar;
import views.html.hourentry.hourEntriesTable;
import beans.HourEntryForm;
import beans.MultipleHourEntries;
import datastructures.TotalsDay;
import datastructures.overview.calendar.CalendarMonth;
import datastructures.overview.week.Week;
import be.objectify.deadbolt.actions.Restrict;

@Security.Authenticated(Secured.class)
@Restrict("user")
public class HourEntries extends Controller {

	@Transactional(readOnly = true)
	public static Result add() {
		final User user = Application.getCurrentUser();
		Form<HourEntryForm> newForm = form(HourEntryForm.class);
		List<HourEntry> entries = HourEntry.findAllForUserForDay(user,
				new LocalDate());
		return ok(createHourEntry.render(newForm, entries));
	}

	@Transactional
	public static Result create() {
		final User user = Application.getCurrentUser();
		Form<HourEntryForm> filledForm = form(HourEntryForm.class)
				.bindFromRequest();

		List<HourEntry> entries = HourEntry.findAllForUserForDay(user,
				new LocalDate());

		if (filledForm.hasErrors())
			return badRequest(createHourEntry.render(filledForm, entries));

		filledForm.get().toHourEntry().save();
		return redirect(routes.HourEntries.addForDay(filledForm.get().date));
	}

	@Transactional(readOnly = true)
	public static Result addForDay(LocalDate date) {
		final User user = Application.getCurrentUser();
		Form<HourEntryForm> newForm = form(HourEntryForm.class);

		List<HourEntry> entries = HourEntry.findAllForUserForDay(user, date);
		TotalsDay totalsToday = HourEntry.findTotalsForUserForDay(user, date);

		return ok(createHourEntryForDay.render(newForm, date, entries,
				totalsToday));
	}

	@Transactional
	public static Result createForDay(LocalDate date) {
		final User user = Application.getCurrentUser();
		Form<HourEntryForm> filledForm = form(HourEntryForm.class)
				.bindFromRequest();

		List<HourEntry> entries = HourEntry.findAllForUserForDay(user, date);
		TotalsDay totalsToday = HourEntry.findTotalsForUserForDay(user, date);

		if (filledForm.hasErrors())
			return badRequest(createHourEntryForDay.render(filledForm, date,
					entries, totalsToday));

		filledForm.get().toHourEntry().save();
		return redirect(routes.HourEntries.addForDay(date));
	}

	@Transactional(readOnly = true)
	public static Result addMultiple() {
		final User user = Application.getCurrentUser();
		Form<MultipleHourEntries> newForm = form(MultipleHourEntries.class);
		return ok(createHourEntries.render(newForm));
	}

	@Transactional
	public static Result createMultiple() {
		final User user = Application.getCurrentUser();
		Form<MultipleHourEntries> filledForm = form(MultipleHourEntries.class)
				.bindFromRequest();

		if (filledForm.hasErrors()) {
			return badRequest(createHourEntries.render(filledForm));
		}

		MultipleHourEntries entries = filledForm.get();
		for (HourEntryForm entryForm : entries.hourEntryForms) {
			entryForm.toHourEntry().save();
		}

		return redirect(routes.HourEntries.calendarOverview());
	}

	@Transactional(readOnly = true)
	public static Result addForWeek(int weekyear, int weekOfWeekyear) {
		Week week = new Week(Application.getCurrentUser(), weekyear, weekOfWeekyear);
		return ok(createHourEntriesForWeek.render(week));
	}

	@Transactional()
	public static Result createForWeek(int weekyear, int weekOfWeekyear) {
		Form<Week> filledForm = form(Week.class).bindFromRequest();

		// Binding errors
		if (filledForm.hasErrors()) {
			Week week = new Week(Application.getCurrentUser(), weekyear, weekOfWeekyear);
			return badRequest(createHourEntriesForWeek.render(week));
		}

		Week week = filledForm.get();

		// Invalid data
		if (!week.isValid()) {
			return badRequest(createHourEntriesForWeek.render(week));
		}

		week.process(Application.getCurrentUser());
		return ok(createHourEntriesForWeek.render(week));
	}

	@Transactional(readOnly = true)
	public static Result edit(Long entryId) {
		final User user = Application.getCurrentUser();
		final HourEntry hourEntry = HourEntry.findById(entryId);

		if (user.hasProjectManagerRole() && hourEntry.assignment.project.projectManager.equals(user)) {
			Form<HourEntryForm> newForm = form(HourEntryForm.class).fill(new HourEntryForm(HourEntry.findById(entryId)));
			return ok(editHourEntryForProjectManager.render(entryId, newForm));
		} else {
			if (hourEntry.assignment.user.equals(user)) {
				Form<HourEntryForm> newForm = form(HourEntryForm.class).fill(new HourEntryForm(HourEntry.findById(entryId)));
				return ok(editHourEntry.render(entryId, newForm));
			} else {
				return forbidden(views.html.forbidden.render());
			}
		}
	}

	@Transactional
	public static Result update(Long entryId) {
		final User user = Application.getCurrentUser();
		Form<HourEntryForm> filledForm = form(HourEntryForm.class)
				.bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editHourEntry.render(entryId, filledForm));

		filledForm.get().toHourEntry().update(entryId);
		return redirect(routes.HourEntries.addForDay(filledForm.get().date));
	}

	@Transactional
	public static Result delete(Long entryId) {
		HourEntry entry = HourEntry.findById(entryId);
		entry.delete();
		return redirect(routes.HourEntries.addForDay(entry.date));
	}

	@Transactional(readOnly = true)
	public static Result tableOverview() {
		final User user = Application.getCurrentUser();
		LocalDate currentDate = new LocalDate();
		return ok(hourEntriesTable.render(HourEntry
				.findTotalsForUserPerAssignmentBetween(user,
						DateUtil.firstDateOfMonth(currentDate),
						DateUtil.lastDateOfMonth(currentDate))));
	}

	@Transactional(readOnly = true)
	public static Result calendarOverview() {
		final User user = Application.getCurrentUser();
		LocalDate currentDate = new LocalDate();
		CalendarMonth calendar = new CalendarMonth(currentDate, user);
		return ok(hourEntriesCalendar.render(calendar));
	}

}
