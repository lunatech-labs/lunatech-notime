package controllers;

import models.HourEntry;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.user.hourentry.*;

public class HourEntries extends Controller {
	
	@Transactional(readOnly=true)
	public static Result allFor(Long userId) {
		return ok(hourEntries.render(userId, HourEntry.allFor(userId)));
	}
	
	@Transactional(readOnly=true)
	public static Result add(Long userId) {
		Form<HourEntry> newForm = form(HourEntry.class);
		return ok(createHourEntry.render(userId, newForm));
	}
	
	@Transactional
	public static Result create(Long userId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();
		
		if(filledForm.hasErrors()) 
			return badRequest(createHourEntry.render(userId, filledForm));
		
		HourEntry entry = filledForm.get();
		
		if(!HourEntry.hasValidDate(entry)) {
			flash("error", HourEntry.validateDate(entry));
			return badRequest(createHourEntry.render(userId, filledForm));
		}	
		
		String tagsString = filledForm.field("tagsString").value();
		HourEntry.create(entry, tagsString);
		return redirect(routes.HourEntries.allFor(userId));
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long userId, Long entryId) {
		Form<HourEntry> newForm = form(HourEntry.class).fill(HourEntry.read(entryId));
		return ok(editHourEntry.render(userId, entryId, newForm));
	}
	
	@Transactional
	public static Result update(Long userId, Long entryId) {
		Form<HourEntry> filledForm = form(HourEntry.class).bindFromRequest();
		
		if(filledForm.hasErrors())
			return badRequest(editHourEntry.render(userId, entryId, filledForm));
		
		HourEntry entry = filledForm.get();
		
		if(!HourEntry.hasValidDate(entry)) {
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
