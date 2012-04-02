package util.form.beans.hourentry;

import java.util.List;


import models.HourEntry;

public class HourEntriesWeek {

	public List<UnannotatedHourEntry> hourEntries;

	/**
	 * Validates the binded data. For each entry are 4 possible actions: 
	 * 1. The id is null: 
	 *   1.1. Hours & minutes are null or 0 -> do nothing 
	 *   1.2. Hours & minutes have a value -> validate value and create @{link HourEntry} 
	 * 2. The id is not null: 
	 *   2.1. Hours & minutes are null or 0 -> delete @{link HourEntry}
	 *   2.2. Hours & minutes have a value -> update @{link HourEntry}
	 */
	public void validateAndProces() {
		if (hourEntries != null) {
			for (UnannotatedHourEntry entry : hourEntries) {
				if (entry.id == null) {
					if (!entry.hasNullOrZeroHours() || !entry.hasNullOrZeroMinutes()) {
						entry.setHoursAndMinutesFromNullToZero();
						if (entry.isValid()) {
							new HourEntry(entry).save("");
						}
					}
				} else {
					if (entry.hasNullOrZeroHours() && entry.hasNullOrZeroMinutes()) {
						HourEntry hourEntry = HourEntry.findById(entry.id);
						hourEntry.delete();
					} else {
						entry.setHoursAndMinutesFromNullToZero();
						if (entry.isValid()) {
							new HourEntry(entry).update(entry.id, "");
						}
					}
				}
			}
		}
	}

}
