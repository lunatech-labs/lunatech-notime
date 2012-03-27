package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import models.HourEntry;

import org.joda.time.DateTime;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import util.DateTimeUtil;
import util.Transformers;
import util.datastructures.HourEntriesList;
import util.datastructures.calendar.CalendarMonth;
import util.datastructures.weekoverview.HourEntriesWeekTable;
import views.html.user.hourentry.createHourEntries;
import views.html.user.hourentry.createHourEntriesForWeek;
import views.html.user.hourentry.createHourEntry;
import views.html.user.hourentry.editHourEntry;
import views.html.user.hourentry.hourEntries;
import views.html.user.hourentry.hourEntriesCalendar;
import views.html.user.hourentry.hourEntriesTable;

import com.google.common.collect.Collections2;

public class HourEntries extends Controller {

	@Transactional(readOnly = true)
	public static Result allFor(Long userId) {
		return ok(hourEntries.render(userId, HourEntry.findAllForUser(userId)));
	}

	@Transactional(readOnly = true)
	public static Result tableOverview(Long userId) {
		DateTime currentDate = new DateTime();
		return ok(hourEntriesTable.render(userId, HourEntry
				.getTotalsForUserPerAssignmentBetween(userId,
						DateTimeUtil.firstDateOfMonth(currentDate),
						DateTimeUtil.lastDateOfMonth(currentDate))));
	}

	@Transactional(readOnly = true)
	public static Result calendarOverview(Long userId) {
		DateTime currentDate = new DateTime();
		CalendarMonth calendar = new CalendarMonth(currentDate, userId);
		return ok(hourEntriesCalendar.render(
				userId,
				calendar));
	}

	@Transactional(readOnly = true)
	public static Result add(Long userId) {
		Form<HourEntry> newForm = form(HourEntry.class);
		return ok(createHourEntry.render(userId, newForm));
	}
	
	@Transactional(readOnly = true)
	public static Result addForDay(Long userId, DateTime date) {
		HourEntry defaultValues = new HourEntry();
		defaultValues.date = date;
		System.out.println(date.getWeekyear());
		Form<HourEntry> newForm = form(HourEntry.class).fill(defaultValues);		
		return ok(createHourEntry.render(userId, newForm));
	}

	@Transactional(readOnly = true)
	public static Result addMultiple(Long userId) {
		Form<HourEntriesList> newForm = form(HourEntriesList.class);
		List<Integer> indices = new ArrayList<Integer>();
		indices.add(0);
		return ok(createHourEntries.render(userId, newForm, indices));
	}

	@Transactional(readOnly = true)
	public static Result addForWeek(Long userId) {
		// Some code for TIME-74
		// DateTime currentDate = new DateTime();
		// List<HourEntry> hourEntries = HourEntry.allBetween(userId,
		// DateUtil.firstDateOfWeek(currentDate),
		// DateUtil.lastDateOfWeek(currentDate));
		//
		// Map<Project, List<HourEntry>> entriesPerProject = new
		// HashMap<Project, List<HourEntry>>();
		// for(HourEntry hourEntry : hourEntries) {
		// if(entriesPerProject.containsKey(hourEntry.assignment.project)) {
		// List<HourEntry> entries =
		// entriesPerProject.get(hourEntry.assignment.project);
		// entries.add(hourEntry);
		// entriesPerProject.put(hourEntry.assignment.project, entries);
		// }
		// else {
		// List<HourEntry> entries = new ArrayList<HourEntry>();
		// entries.add(hourEntry);
		// entriesPerProject.put(hourEntry.assignment.project, entries);
		// }
		// }
		//
		// Map<Customer, Map<Project, List<HourEntry>>> projectsPerCustomer =
		// new HashMap<Customer, Map<Project, List<HourEntry>>>();
		// //Some for loop? Ugly!
		

		
		
		HourEntriesWeekTable week = new HourEntriesWeekTable(new DateTime(), userId);

		return ok(createHourEntriesForWeek.render(userId, week));

	}

	@Transactional
	public static Result create(Long userId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();
		
		if (filledForm.hasErrors())
			return badRequest(createHourEntry.render(userId, filledForm));

		String tagsString = filledForm.field("tagsString").value();
		filledForm.get().save(tagsString);
		return redirect(routes.HourEntries.allFor(userId));
	}

	@Transactional
	public static Result createMultiple(Long userId) {
		Form<HourEntriesList> filledForm = form(HourEntriesList.class)
				.bindFromRequest();

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

		HourEntriesList entries = filledForm.get();
		for (int i = 0; i < entries.hourEntries.size(); i++) {
			String tagsString = filledForm.field(
					"hourEntries[" + i + "].tagsString").value();
			entries.hourEntries.get(i).save(tagsString);
		}

		return redirect(routes.HourEntries.allFor(userId));
	}
	
	@Transactional
	public static Result createForWeek(Long userId) {
		System.out.println(request().body().asFormUrlEncoded());
		Form<HourEntriesList> filledForm = form(HourEntriesList.class)
				.bindFromRequest();
		System.out.println(filledForm.errors());
		System.out.println(Integer.parseInt(request().body().asFormUrlEncoded().get("hourEntries[1].hours")[0]));
		return redirect(routes.HourEntries.addForWeek(userId));
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
		return redirect(routes.HourEntries.allFor(userId));
	}

	@Transactional
	public static Result delete(Long userId, Long entryId) {
		HourEntry.findById(entryId).delete();
		return redirect(routes.HourEntries.allFor(userId));
	}

}
