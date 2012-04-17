package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.HourEntry;

import org.joda.time.LocalDate;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtil;
import utils.Transformers;
import views.html.user.hourentry.createHourEntries;
import views.html.user.hourentry.createHourEntriesForWeek;
import views.html.user.hourentry.createHourEntry;
import views.html.user.hourentry.createHourEntryForDay;
import views.html.user.hourentry.editHourEntry;
import views.html.user.hourentry.hourEntriesCalendar;
import views.html.user.hourentry.hourEntriesTable;

import beans.MultipleHourEntries;

import com.google.common.collect.Collections2;

import datastructures.TotalsDay;
import datastructures.overview.calendar.CalendarMonth;
import datastructures.overview.week.Week;

public class HourEntries extends Controller {

	@Transactional(readOnly = true)
	public static Result add(Long userId) {
		Form<HourEntry> newForm = form(HourEntry.class);
		List<HourEntry> entries = HourEntry.findAllForUserForDay(userId,
				new LocalDate());
		return ok(createHourEntry.render(userId, newForm, entries));
	}

	@Transactional
	public static Result create(Long userId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();

		List<HourEntry> entries = HourEntry.findAllForUserForDay(userId,
				new LocalDate());

		if (filledForm.hasErrors())
			return badRequest(createHourEntry.render(userId, filledForm,
					entries));

		String tagsString = filledForm.field("tagsString").value();
		filledForm.get().save(tagsString);
		return redirect(routes.HourEntries.addForDay(userId,
				filledForm.get().date));
	}

	@Transactional(readOnly = true)
	public static Result addForDay(Long userId, LocalDate date) {
		HourEntry defaultValues = new HourEntry();
		defaultValues.hours = 0;
		defaultValues.minutes = 0;
		Form<HourEntry> newForm = form(HourEntry.class).fill(defaultValues);

		List<HourEntry> entries = HourEntry.findAllForUserForDay(userId, date);
		TotalsDay totalsToday = HourEntry.findTotalsForUserForDay(userId, date);

		return ok(createHourEntryForDay.render(userId, newForm, date, entries,
				totalsToday));
	}

	@Transactional
	public static Result createForDay(Long userId, LocalDate date) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();

		List<HourEntry> entries = HourEntry.findAllForUserForDay(userId, date);
		TotalsDay totalsToday = HourEntry.findTotalsForUserForDay(userId, date);

		if (filledForm.hasErrors())
			return badRequest(createHourEntryForDay.render(userId, filledForm,
					date, entries, totalsToday));

		String tagsString = filledForm.field("tagsString").value();
		filledForm.get().save(tagsString);
		return redirect(routes.HourEntries.addForDay(userId, date));
	}

	@Transactional(readOnly = true)
	public static Result addMultiple(Long userId) {
		Form<MultipleHourEntries> newForm = form(MultipleHourEntries.class);
		List<Integer> indices = new ArrayList<Integer>() {
			{
				add(0);
			}
		};
		return ok(createHourEntries.render(userId, newForm, indices));
	}

	@Transactional
	public static Result createMultiple(Long userId) {
		Form<MultipleHourEntries> filledForm = form(
				MultipleHourEntries.class).bindFromRequest();

		if (filledForm.hasErrors()) {
			// Get the indices of the submitted form-inputs
			Set<String> keys = filledForm.data().keySet();
			// Remove strange key
			keys.remove("hourEntries[");
			Collection<Integer> indices = Collections2.transform(keys,
					Transformers.indexTransformer);
			Set<Integer> uniqueIndices = new HashSet<Integer>(indices);

			return badRequest(createHourEntries.render(userId, filledForm,
					new ArrayList<Integer>(uniqueIndices)));
		}

		MultipleHourEntries entries = filledForm.get();
		for (int i = 0; i < entries.hourEntries.size(); i++) {
			String tagsString = filledForm.field(
					"hourEntries[" + i + "].tagsString").value();
			entries.hourEntries.get(i).save(tagsString);
		}

		return redirect(routes.HourEntries.calendarOverview(userId));
	}

	@Transactional(readOnly = true)
	public static Result addForWeek(Long userId, int weekyear,
			int weekOfWeekyear) {
		Week week = new Week(userId, weekyear, weekOfWeekyear);
		return ok(createHourEntriesForWeek.render(userId, week));
	}

	@Transactional()
	public static Result createForWeek(Long userId, int weekyear,
			int weekOfWeekyear) {
		Form<Week> filledForm = form(Week.class).bindFromRequest();
		Week week = filledForm.get();

		if (!week.isValid()) {
			return badRequest(createHourEntriesForWeek.render(userId, week));
		}

		week.process(userId);
		return ok(createHourEntriesForWeek.render(userId, week));
	}

	@Transactional(readOnly = true)
	public static Result edit(Long userId, Long entryId) {
		Form<HourEntry> newForm = form(HourEntry.class).fill(
				HourEntry.findById(entryId));
		return ok(editHourEntry.render(userId, entryId, newForm));
	}

	@Transactional
	public static Result update(Long userId, Long entryId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editHourEntry.render(userId, entryId, filledForm));

		String tagsString = filledForm.field("tagsString").value();
		filledForm.get().update(entryId, tagsString);
		return redirect(routes.HourEntries.addForDay(userId,
				filledForm.get().date));
	}

	@Transactional
	public static Result delete(Long userId, Long entryId) {
		HourEntry entry = HourEntry.findById(entryId);
		entry.delete();
		return redirect(routes.HourEntries.addForDay(userId, entry.date));
	}

	@Transactional(readOnly = true)
	public static Result tableOverview(Long userId) {
		LocalDate currentDate = new LocalDate();
		return ok(hourEntriesTable.render(userId, HourEntry
				.findTotalsForUserPerAssignmentBetween(userId,
						DateUtil.firstDateOfMonth(currentDate),
						DateUtil.lastDateOfMonth(currentDate))));
	}

	@Transactional(readOnly = true)
	public static Result calendarOverview(Long userId) {
		LocalDate currentDate = new LocalDate();
		CalendarMonth calendar = new CalendarMonth(currentDate, userId);
		return ok(hourEntriesCalendar.render(userId, calendar));
	}

}
