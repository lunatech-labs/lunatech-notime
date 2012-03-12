package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sun.org.apache.bcel.internal.generic.NEW;

import models.HourEntry;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import util.Transformers;
import util.form.binding.HourEntriesList;
import views.html.user.hourentry.createHourEntries;
import views.html.user.hourentry.createHourEntriesForWeek;
import views.html.user.hourentry.createHourEntry;
import views.html.user.hourentry.editHourEntry;
import views.html.user.hourentry.hourEntries;

public class HourEntries extends Controller {

	@Transactional(readOnly = true)
	public static Result allFor(Long userId) {
		return ok(hourEntries.render(userId, HourEntry.allFor(userId)));
	}

	@Transactional(readOnly = true)
	public static Result add(Long userId) {
		Form<HourEntry> newForm = form(HourEntry.class);
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

		return ok(createHourEntriesForWeek.render(userId));

	}

	@Transactional
	public static Result create(Long userId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createHourEntry.render(userId, filledForm));

		HourEntry entry = filledForm.get();

		if (!HourEntry.hasValidDate(entry)) {
			flash("error", HourEntry.validateDate(entry));
			return badRequest(createHourEntry.render(userId, filledForm));
		}

		String tagsString = filledForm.field("tagsString").value();
		HourEntry.create(entry, tagsString);
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
			HourEntry.create(entries.hourEntries.get(i), tagsString);
		}

		return redirect(routes.HourEntries.allFor(userId));
	}

	@Transactional(readOnly = true)
	public static Result edit(Long userId, Long entryId) {
		Form<HourEntry> newForm = form(HourEntry.class).fill(
				HourEntry.read(entryId));
		return ok(editHourEntry.render(userId, entryId, newForm));
	}

	@Transactional
	public static Result update(Long userId, Long entryId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editHourEntry.render(userId, entryId, filledForm));

		HourEntry entry = filledForm.get();

		if (!HourEntry.hasValidDate(entry)) {
			flash("error", HourEntry.validateDate(entry));
			return badRequest(editHourEntry.render(userId, entryId, filledForm));
		}

		String tagsString = filledForm.field("tagsString").value();
		HourEntry.update(entryId, entry, tagsString);
		return redirect(routes.HourEntries.allFor(userId));
	}

	@Transactional
	public static Result delete(Long userId, Long entryId) {
		HourEntry.delete(entryId);
		return redirect(routes.HourEntries.allFor(userId));
	}

}
