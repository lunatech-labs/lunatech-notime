package util.datastructures;

import java.util.List;

import javax.validation.Valid;

import models.HourEntry;

/**
 * Datastructure for binding a list of {@link HourEntry} objects
 */
public class HourEntriesList {

	@Valid
	public List<HourEntry> hourEntries;

}
