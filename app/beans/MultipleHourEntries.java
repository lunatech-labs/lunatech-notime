package beans;

import java.util.List;

import javax.validation.Valid;

import models.HourEntry;

/**
 * Datastructure for binding a list of {@link HourEntry} objects
 */
public class MultipleHourEntries {

	@Valid
	public List<HourEntryForm> hourEntryForms;

	public String validate() {
		if (hourEntryForms == null) {
			return "No entries submitted";
		} else {
			for (HourEntryForm entryForm : hourEntryForms) {
				if (entryForm.validate() != null)
					return entryForm.validate();
			}
			return null;
		}		
	}

}
