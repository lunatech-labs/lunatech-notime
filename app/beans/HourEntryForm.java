package beans;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import models.HourEntry;
import models.ProjectAssignment;
import models.Tag;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Required;

public class HourEntryForm {

	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public ProjectAssignment assignment;

	@Required
	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
	public LocalDate date;

	@Required
	@Min(0)
	@Max(23)
	public Integer hours;

	@Required
	@Min(0)
	@Max(59)
	public Integer minutes;

	public String tagsString;

	public boolean billable;

	@Min(0)
	public Integer rate;

	public HourEntryForm() {
	}

	public HourEntryForm(HourEntry entry) {
		assignment = entry.assignment;
		date = entry.date;
		hours = entry.hours;
		minutes = entry.minutes;
		tagsString = entry.enteredTagsString();
		billable = entry.billable;
		rate = entry.rate;
	}

	public List<Tag> getTags() {
		if (!tagsString.isEmpty()) {
			List<Tag> tags = new LinkedList<Tag>();
			String splittedTags[] = tagsString.split(";");
			for (int i = 0; i < splittedTags.length; i++)
				tags.add(Tag.findOrCreate(splittedTags[i]));
			return tags;
		}
		return Collections.emptyList();
	}
	
	public List<Tag> getRequiredTags() {
		return ProjectAssignment.findById(assignment.id).project.requiredTags;
	}

	public HourEntry toHourEntry() {
		return new HourEntry(this);
	}

	public String validate() {
		if (!isValidAssignment())
			return "Project is not valid!";
		if (!isDateInRange())
			return "Date is not in assigned range!";
		if (!containsRequiredTags())
			return "Doesn't contain any of the required tags ("
					+ Tag.tagsToString(getRequiredTags()) + ")";
		return null;
	}

	public boolean isValidAssignment() {
		return assignment.id != null
				&& ProjectAssignment.findById(assignment.id).active;
	}

	public boolean isDateInRange() {
		return ProjectAssignment.isDateInAssignmentRange(date, assignment.id);
	}

	public boolean containsRequiredTags() {
		List<Tag> requiredTags = getRequiredTags();
		if (requiredTags == null || requiredTags.isEmpty())
			return true; // No required tags, so entered tags are valid

		if (tagsString == null)
			return false; // There are required tags, but no tags are submitted

		int occurences = 0;
		for (Tag tag : getTags()) {
			if (requiredTags.contains(tag))
				occurences++;
		}

		if (occurences == 0)
			return false; // None of the submitted tag(s) matched the required tag(s)

		return true;
	}

}
