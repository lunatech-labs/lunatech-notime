package util.form.binding;

import java.util.List;

import javax.validation.Valid;

import models.HourEntry;

public class HourEntriesList {
	
	@Valid
	public List<HourEntry> hourEntries;

}
