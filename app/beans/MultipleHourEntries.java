package beans;

import java.util.List;

import javax.validation.Valid;

import models.HourEntry;

/**
 * Datastructure for binding a list of {@link HourEntry} objects
 */
public class MultipleHourEntries {

	@Valid
	public List<HourEntry> hourEntries;

	public String validate() {
		if (hourEntries == null) {
			return "No entries submitted";
		} else {
			for (HourEntry entry : hourEntries) {
				if (entry.validate() != null)
					return entry.validate();
			}
			return null;
		}		
	}

}
